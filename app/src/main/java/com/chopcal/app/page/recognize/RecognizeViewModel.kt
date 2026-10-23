package com.chopcal.app.page.recognize

import com.chopcal.app.domain.model.CaptureMealTarget
import com.chopcal.app.domain.model.CookingMethod
import com.chopcal.app.domain.model.DishCandidate
import com.chopcal.app.domain.model.DishDraft
import com.chopcal.app.domain.model.MealRecord
import com.chopcal.app.domain.model.MealType
import com.chopcal.app.domain.model.Portion
import com.chopcal.app.domain.model.RecipeLine
import com.chopcal.app.domain.repository.HealthTipRepository
import com.chopcal.app.domain.repository.IngredientRepository
import com.chopcal.app.domain.repository.UserProfileRepository
import com.chopcal.app.domain.usecase.BuildDishDraftUseCase
import com.chopcal.app.domain.usecase.NutrientComparatorUseCase
import com.chopcal.app.domain.usecase.RecognizeDishUseCase
import com.chopcal.app.domain.usecase.SaveMealRecordsUseCase
import com.chopcal.app.domain.usecase.SearchDishUseCase
import com.rainbow.compose.core.contract.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * 识别流程 ViewModel：拍照/相册 → 识别 → 候选 → 核对食材 → 结果（一餐多菜）→ 保存。
 */
@HiltViewModel
class RecognizeViewModel @Inject constructor(
    private val recognizeDish: RecognizeDishUseCase,
    private val buildDishDraft: BuildDishDraftUseCase,
    private val searchDish: SearchDishUseCase,
    private val ingredientRepository: IngredientRepository,
    private val healthTipRepository: HealthTipRepository,
    private val nutrientComparator: NutrientComparatorUseCase,
    private val profileRepository: UserProfileRepository,
    private val saveMealRecords: SaveMealRecordsUseCase,
) : BaseMviViewModel<RecognizeIntent, RecognizeEffect>() {

    private val _uiState = MutableStateFlow(RecognizeUiState())
    val uiState: StateFlow<RecognizeUiState> = _uiState.asStateFlow()

    override suspend fun handleIntent(intent: RecognizeIntent) {
        when (intent) {
            is RecognizeIntent.Open -> onOpen(intent.dateEpochDay, intent.target, intent.imageUri)
            is RecognizeIntent.SelectMealTarget -> _uiState.update { it.copy(target = intent.target) }
            is RecognizeIntent.StartCapture -> onStartCapture(intent.imageUri)
            RecognizeIntent.RetryRecognize -> onStartCapture(null)
            RecognizeIntent.BackToCandidates -> _uiState.update {
                it.copy(stage = RecognizeStage.CANDIDATES, searchQuery = "", searchResults = emptyList())
            }
            is RecognizeIntent.SelectCandidate -> onSelectCandidate(intent.index)
            RecognizeIntent.NotThisDish -> _uiState.update {
                it.copy(stage = RecognizeStage.SEARCH, searchQuery = "", searchResults = emptyList())
            }
            is RecognizeIntent.SearchDish -> onSearchDish(intent.query)
            is RecognizeIntent.SelectSearchResult -> onSelectSearchResult(intent.dish)
            is RecognizeIntent.SearchIngredient -> onSearchIngredient(intent.query)
            is RecognizeIntent.AddIngredient -> onAddIngredient(intent.ingredient, intent.amountG)
            is RecognizeIntent.SelectPortion -> updateDraft { it.copy(portion = intent.portion) }
            is RecognizeIntent.SelectCookingMethod -> updateDraft { it.copy(method = intent.method) }
            is RecognizeIntent.UpdateServings -> updateDraft { it.copy(servings = intent.servings.coerceIn(1, 10)) }
            is RecognizeIntent.EditLineAmount -> updateDraft { it.withLineAmount(intent.index, intent.amountG) }
            is RecognizeIntent.RemoveLine -> updateDraft { it.withoutLine(intent.index) }
            RecognizeIntent.ComputeResult -> onComputeResult()
            RecognizeIntent.ContinueAdd -> _uiState.update {
                it.copy(stage = RecognizeStage.INPUT, dishDraft = null, candidates = emptyList(), candidateKcals = emptyList())
            }
            RecognizeIntent.SaveResult -> onSaveResult()
            RecognizeIntent.SaveAdd -> onSaveWithMode(SaveMealRecordsUseCase.Mode.ADD)
            RecognizeIntent.SaveReplace -> onSaveWithMode(SaveMealRecordsUseCase.Mode.REPLACE)
            RecognizeIntent.CancelSave -> _uiState.update { it.copy(saveConflict = null) }
            RecognizeIntent.Dismiss -> {
                reset()
                sendEffect(RecognizeEffect.Finished)
            }
        }
    }

    private suspend fun onOpen(dateEpochDay: Long, target: CaptureMealTarget, imageUri: String?) {
        val dailyTarget = profileRepository.dailyTarget.first()
        _uiState.value = RecognizeUiState(
            dateEpochDay = dateEpochDay,
            target = target,
            healthTips = healthTipRepository.all(),
            dailyTargetKcal = dailyTarget?.dailyKcal ?: 0,
        )
        if (imageUri != null) {
            onStartCapture(imageUri)
        }
    }

    private suspend fun onStartCapture(imageUri: String?) {
        _uiState.update { it.copy(stage = RecognizeStage.RECOGNIZING, candidates = emptyList()) }
        val candidates = runCatching { recognizeDish(imageUri) }.getOrDefault(emptyList())
        val candidateKcals = candidates.map { candidate ->
            buildDishDraft(candidate, Portion.MEDIUM, CookingMethod.HOME_STYLE, 1)
                ?.nutrients()?.kcalTypical ?: 0
        }
        _uiState.update {
            it.copy(
                stage = RecognizeStage.CANDIDATES,
                candidates = candidates,
                candidateKcals = candidateKcals,
                selectedCandidateIndex = 0,
            )
        }
    }

    private fun onSelectCandidate(index: Int) {
        val candidate = _uiState.value.candidates.getOrNull(index) ?: return
        applyDraft(candidate, Portion.MEDIUM)
    }

    private fun onSearchDish(query: String) {
        _uiState.update {
            it.copy(searchQuery = query, searchResults = if (query.isBlank()) emptyList() else searchDish(query))
        }
    }

    private fun onSelectSearchResult(dish: com.chopcal.app.domain.model.Dish) {
        val candidate = DishCandidate(name = dish.name, confidence = 100, matchedDish = dish)
        applyDraft(candidate, Portion.MEDIUM)
    }

    private fun applyDraft(candidate: DishCandidate, portion: Portion) {
        val method = candidate.matchedDish?.cookingMethod ?: CookingMethod.HOME_STYLE
        val draft = buildDishDraft(candidate, portion, method, 1)
        _uiState.update {
            it.copy(
                dishDraft = draft,
                stage = RecognizeStage.ADJUST,
                searchQuery = "",
                searchResults = emptyList(),
            )
        }
    }

    private fun onSearchIngredient(query: String) {
        _uiState.update {
            it.copy(
                ingredientQuery = query,
                ingredientResults = if (query.isBlank()) emptyList() else ingredientRepository.search(query),
            )
        }
    }

    private fun onAddIngredient(ingredient: com.chopcal.app.domain.model.Ingredient, amountG: Double) {
        if (amountG <= 0.0) return
        val line = RecipeLine(
            name = ingredient.name,
            role = "自定义",
            amountG = amountG,
            kcalPer100g = ingredient.kcalPer100g,
            proteinGPer100g = ingredient.proteinGPer100g,
            carbsGPer100g = ingredient.carbsGPer100g,
            fatGPer100g = ingredient.fatGPer100g,
        )
        updateDraft { it.withLine(line) }
        _uiState.update { it.copy(ingredientQuery = "", ingredientResults = emptyList()) }
    }

    private fun updateDraft(transform: (DishDraft) -> DishDraft) {
        _uiState.update { state ->
            val draft = state.dishDraft ?: return@update state
            state.copy(dishDraft = transform(draft))
        }
    }

    private fun onComputeResult() {
        val state = _uiState.value
        val draft = state.dishDraft ?: return
        val newDrafts = state.drafts + draft
        val totalKcal = newDrafts.sumOf { it.nutrients().kcalTypical }
        _uiState.update {
            it.copy(
                stage = RecognizeStage.RESULT,
                drafts = newDrafts,
                comparison = nutrientComparator(totalKcal, state.dailyTargetKcal),
            )
        }
    }

    private suspend fun onSaveResult() {
        val state = _uiState.value
        if (state.drafts.isEmpty()) return
        if (state.target !is CaptureMealTarget.Meal) {
            // 仅查询：MVP 不入餐次/当天摄入（记录列表标记为后续特性）
            reset()
            sendEffect(RecognizeEffect.Finished)
            return
        }
        val mealType = state.target.mealType
        val existing = saveMealRecords.existing(state.dateEpochDay, mealType)
        if (existing.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    saveConflict = SaveConflict(
                        existingKcal = existing.sumOf { m -> m.kcal },
                        existingCount = existing.size,
                    )
                )
            }
        } else {
            saveDrafts(state, mealType, SaveMealRecordsUseCase.Mode.ADD)
        }
    }

    private suspend fun onSaveWithMode(mode: SaveMealRecordsUseCase.Mode) {
        val state = _uiState.value
        val mealType = (state.target as? CaptureMealTarget.Meal)?.mealType ?: return
        saveDrafts(state, mealType, mode)
    }

    private suspend fun saveDrafts(state: RecognizeUiState, mealType: MealType, mode: SaveMealRecordsUseCase.Mode) {
        val records = state.drafts.map { draft ->
            val n = draft.nutrients()
            MealRecord(
                dateEpochDay = state.dateEpochDay,
                mealType = mealType,
                dishName = draft.dishName,
                kcal = n.kcalTypical,
                kcalMin = n.kcalMin,
                kcalMax = n.kcalMax,
                proteinG = n.proteinG,
                carbsG = n.carbsG,
                fatG = n.fatG,
                ingredientsJson = draft.lines.joinToString(";") { "${it.name}=${it.amountG}g" },
            )
        }
        saveMealRecords(state.dateEpochDay, mealType, records, mode)
        reset()
        sendEffect(RecognizeEffect.Finished)
    }

    private fun reset() {
        _uiState.value = RecognizeUiState()
    }
}
