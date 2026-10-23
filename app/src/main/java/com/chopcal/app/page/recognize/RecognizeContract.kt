package com.chopcal.app.page.recognize

import com.chopcal.app.domain.model.CaptureMealTarget
import com.chopcal.app.domain.model.CookingMethod
import com.chopcal.app.domain.model.Dish
import com.chopcal.app.domain.model.DishCandidate
import com.chopcal.app.domain.model.DishDraft
import com.chopcal.app.domain.model.HealthTip
import com.chopcal.app.domain.model.Ingredient
import com.chopcal.app.domain.model.NutrientTotal
import com.chopcal.app.domain.model.Portion
import com.chopcal.app.domain.usecase.NutrientComparatorUseCase
import com.rainbow.compose.core.contract.UiEffect
import com.rainbow.compose.core.contract.UiIntent

/** 识别流程 Intent。 */
sealed interface RecognizeIntent : UiIntent {
    data class Open(val dateEpochDay: Long, val target: CaptureMealTarget, val imageUri: String?) : RecognizeIntent
    data class SelectMealTarget(val target: CaptureMealTarget) : RecognizeIntent
    data class StartCapture(val imageUri: String?) : RecognizeIntent
    data object RetryRecognize : RecognizeIntent
    data object BackToCandidates : RecognizeIntent
    data class SelectCandidate(val index: Int) : RecognizeIntent
    data object NotThisDish : RecognizeIntent
    data class SearchDish(val query: String) : RecognizeIntent
    data class SelectSearchResult(val dish: Dish) : RecognizeIntent
    data class SearchIngredient(val query: String) : RecognizeIntent
    data class AddIngredient(val ingredient: Ingredient, val amountG: Double) : RecognizeIntent
    data class SelectPortion(val portion: Portion) : RecognizeIntent
    data class SelectCookingMethod(val method: CookingMethod) : RecognizeIntent
    data class UpdateServings(val servings: Int) : RecognizeIntent
    data class EditLineAmount(val index: Int, val amountG: Double) : RecognizeIntent
    data class RemoveLine(val index: Int) : RecognizeIntent
    data object ComputeResult : RecognizeIntent
    /** 结果页「继续添加一道菜」。 */
    data object ContinueAdd : RecognizeIntent
    data object SaveResult : RecognizeIntent
    /** 保存冲突：添加到该餐（累计）。 */
    data object SaveAdd : RecognizeIntent
    /** 保存冲突：替换该餐（仅保留本次）。 */
    data object SaveReplace : RecognizeIntent
    /** 保存冲突：取消（返回结果页）。 */
    data object CancelSave : RecognizeIntent
    data object Dismiss : RecognizeIntent
}

/** 识别流程一次性副作用。 */
sealed interface RecognizeEffect : UiEffect {
    data object Finished : RecognizeEffect
}

/** 识别流程阶段。 */
enum class RecognizeStage { INPUT, RECOGNIZING, CANDIDATES, SEARCH, ADJUST, RESULT }

/** 保存到已有餐次时的冲突信息。 */
data class SaveConflict(
    val existingKcal: Int,
    val existingCount: Int,
)

/** 识别流程 UI 状态。 */
data class RecognizeUiState(
    val stage: RecognizeStage = RecognizeStage.INPUT,
    val target: CaptureMealTarget = CaptureMealTarget.QueryOnly,
    val dateEpochDay: Long = 0,
    val candidates: List<DishCandidate> = emptyList(),
    val selectedCandidateIndex: Int = 0,
    /** 每个候选的典型热量预览（约 XXX kcal），与 [candidates] 对齐。 */
    val candidateKcals: List<Int> = emptyList(),
    /** 当前编辑中的菜草稿。 */
    val dishDraft: DishDraft? = null,
    /** 已确认的菜（一餐多菜）。 */
    val drafts: List<DishDraft> = emptyList(),
    val saveConflict: SaveConflict? = null,
    val healthTips: List<HealthTip> = emptyList(),
    val dailyTargetKcal: Int = 0,
    val comparison: NutrientComparatorUseCase.Comparison? = null,
    // 菜品搜索（降级兜底）
    val searchQuery: String = "",
    val searchResults: List<Dish> = emptyList(),
    // 食材搜索（添加食材）
    val ingredientQuery: String = "",
    val ingredientResults: List<Ingredient> = emptyList(),
) {
    val selectedCandidate: DishCandidate? get() = candidates.getOrNull(selectedCandidateIndex)
    val isQueryOnly: Boolean get() = target == CaptureMealTarget.QueryOnly

    /** 一餐多菜合计营养。 */
    val totalNutrients: NutrientTotal
        get() = drafts.fold(NutrientTotal()) { acc, d ->
            val n = d.nutrients()
            NutrientTotal(
                kcalMin = acc.kcalMin + n.kcalMin,
                kcalTypical = acc.kcalTypical + n.kcalTypical,
                kcalMax = acc.kcalMax + n.kcalMax,
                proteinG = acc.proteinG + n.proteinG,
                carbsG = acc.carbsG + n.carbsG,
                fatG = acc.fatG + n.fatG,
            )
        }
}
