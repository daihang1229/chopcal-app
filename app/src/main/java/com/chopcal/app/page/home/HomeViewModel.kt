package com.chopcal.app.page.home

import androidx.lifecycle.viewModelScope
import com.chopcal.app.domain.model.CaptureMealTarget
import com.chopcal.app.domain.model.CookingMethod
import com.chopcal.app.domain.model.MacroTargets
import com.chopcal.app.domain.model.MealType
import com.chopcal.app.domain.model.Portion
import com.chopcal.app.domain.repository.RecordRepository
import com.chopcal.app.domain.repository.UserProfileRepository
import com.chopcal.app.domain.usecase.CalculateCalorieUseCase
import com.chopcal.app.domain.usecase.ComputeMacroTargetsUseCase
import com.chopcal.app.domain.usecase.GetDailySummaryUseCase
import com.chopcal.app.domain.usecase.RecognizeDishUseCase
import com.chopcal.app.domain.usecase.SaveMealRecordUseCase
import com.rainbow.compose.core.contract.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileRepository: UserProfileRepository,
    private val recordRepository: RecordRepository,
    private val getDailySummary: GetDailySummaryUseCase,
    private val computeMacroTargets: ComputeMacroTargetsUseCase,
    private val recognizeDish: RecognizeDishUseCase,
    private val calculateCalorie: CalculateCalorieUseCase,
    private val saveMealRecord: SaveMealRecordUseCase,
) : BaseMviViewModel<HomeIntent, HomeEffect>() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now().toEpochDay())

    init {
        val today = LocalDate.now()
        val todayEpochDay = today.toEpochDay()
        _selectedDate.value = todayEpochDay
        _uiState.value = HomeUiState(
            loading = true,
            selectedDateEpochDay = todayEpochDay,
            todayEpochDay = todayEpochDay,
            week = buildWeek(todayEpochDay, todayEpochDay),
        )

        // 加载每日目标 + 宏量目标
        viewModelScope.launch {
            val target = profileRepository.dailyTarget.first()
            val profile = profileRepository.profile.first()
            val device = profileRepository.device.first()
            _uiState.update {
                it.copy(
                    dailyTarget = target,
                    goalType = profile?.goalType,
                    nickname = device.nickname,
                    macroTargets = target?.let(computeMacroTargets::invoke) ?: MacroTargets(),
                )
            }
        }

        // 观察选中日期的摄入汇总
        viewModelScope.launch {
            _selectedDate.collectLatest { date ->
                getDailySummary(date).collect { summary ->
                    _uiState.update { it.copy(summary = summary, loading = false) }
                }
            }
        }
    }

    override suspend fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SelectDate -> onSelectDate(intent.epochDay)
            is HomeIntent.OpenCapture -> onOpenCapture(defaultMealByTime())
            is HomeIntent.OpenCaptureForMeal -> onOpenCapture(CaptureMealTarget.Meal(intent.mealType))
            is HomeIntent.SelectMealTarget -> onSelectMealTarget(intent.target)
            is HomeIntent.StartCapture -> onStartCapture(intent.imageUri)
            is HomeIntent.DismissCapture -> onDismissCapture()
            is HomeIntent.RetryRecognize -> onStartCapture(null)
            is HomeIntent.BackToCandidates -> _uiState.update { it.copy(captureStage = CaptureStage.CANDIDATES) }
            is HomeIntent.SelectCandidate -> onSelectCandidate(intent.index)
            is HomeIntent.NotThisDish -> onNotThisDish()
            is HomeIntent.SelectPortion -> _uiState.update { it.copy(portion = intent.portion) }
            is HomeIntent.SelectCookingMethod -> _uiState.update { it.copy(cookingMethod = intent.method) }
            is HomeIntent.UpdateServings -> _uiState.update { it.copy(servings = intent.servings.coerceIn(1, 10)) }
            is HomeIntent.ComputeResult -> onComputeResult()
            is HomeIntent.SaveResult -> onSaveResult()
            is HomeIntent.DismissResult -> onDismissCapture()
            is HomeIntent.SetWaterMl -> onSetWaterMl(intent.ml)
            is HomeIntent.SetStepCount -> onSetStepCount(intent.steps)
        }
    }

    // ── 日期 ──────────────────────────────────────────────────

    private fun onSelectDate(epochDay: Long) {
        val today = _uiState.value.todayEpochDay
        if (epochDay > today) return // 未来天不可点
        _selectedDate.value = epochDay
        _uiState.update {
            it.copy(
                selectedDateEpochDay = epochDay,
                week = buildWeek(today, epochDay),
            )
        }
    }

    private fun buildWeek(todayEpochDay: Long, selectedEpochDay: Long): List<DayItem> {
        val selected = LocalDate.ofEpochDay(selectedEpochDay)
        val monday = selected.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return (0L until 7L).map { i ->
            val date = monday.plusDays(i)
            DayItem(
                epochDay = date.toEpochDay(),
                weekdayLabel = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                dayOfMonth = date.dayOfMonth,
                isToday = date.toEpochDay() == todayEpochDay,
                isFuture = date.toEpochDay() > todayEpochDay,
                isSelected = date.toEpochDay() == selectedEpochDay,
            )
        }
    }

    // ── 拍摄弹窗 ──────────────────────────────────────────────

    private fun defaultMealByTime(): CaptureMealTarget {
        val now = LocalTime.now()
        return CaptureMealTarget.Meal(MealType.fromTime(now.hour, now.minute))
    }

    private fun onOpenCapture(target: CaptureMealTarget) {
        _uiState.update {
            it.copy(
                captureVisible = true,
                captureStage = CaptureStage.INPUT,
                captureTarget = target,
                candidates = emptyList(),
                selectedCandidateIndex = 0,
                portion = Portion.MEDIUM,
                cookingMethod = CookingMethod.NORMAL,
                servings = 1,
                calorieResult = null,
            )
        }
    }

    private fun onSelectMealTarget(target: CaptureMealTarget) {
        _uiState.update { it.copy(captureTarget = target) }
    }

    private suspend fun onStartCapture(imageUri: String?) {
        _uiState.update { it.copy(captureStage = CaptureStage.RECOGNIZING, candidates = emptyList()) }
        val candidates = runCatching { recognizeDish(imageUri) }.getOrDefault(emptyList())
        _uiState.update {
            it.copy(
                captureStage = CaptureStage.CANDIDATES,
                candidates = candidates,
                selectedCandidateIndex = 0,
            )
        }
    }

    private fun onSelectCandidate(index: Int) {
        _uiState.update {
            it.copy(
                selectedCandidateIndex = index,
                captureStage = CaptureStage.ADJUST,
            )
        }
    }

    private fun onNotThisDish() {
        // 库外兜底（拆食材）为后续特性，MVP 先进入分量/做法步骤
        _uiState.update { it.copy(captureStage = CaptureStage.ADJUST) }
    }

    private fun onComputeResult() {
        val state = _uiState.value
        val candidate = state.selectedCandidate ?: return
        val result = calculateCalorie(candidate, state.portion, state.cookingMethod, state.servings)
        _uiState.update {
            it.copy(captureStage = CaptureStage.RESULT, calorieResult = result)
        }
    }

    private suspend fun onSaveResult() {
        val state = _uiState.value
        val result = state.calorieResult ?: return
        val target = state.captureTarget
        if (target is CaptureMealTarget.Meal) {
            saveMealRecord(
                dateEpochDay = state.selectedDateEpochDay,
                mealType = target.mealType,
                dishName = result.dishName,
                kcal = result.typicalKcal,
                proteinG = result.proteinG,
                carbsG = result.carbsG,
                fatG = result.fatG,
            )
        }
        onDismissCapture()
    }

    private fun onDismissCapture() {
        _uiState.update {
            it.copy(
                captureVisible = false,
                captureStage = CaptureStage.INPUT,
                candidates = emptyList(),
                selectedCandidateIndex = 0,
                calorieResult = null,
            )
        }
    }

    // ── 水 / 步 ───────────────────────────────────────────────

    private suspend fun onSetWaterMl(ml: Int) {
        recordRepository.setWaterMl(_uiState.value.selectedDateEpochDay, ml)
    }

    private suspend fun onSetStepCount(steps: Int) {
        recordRepository.setStepCount(_uiState.value.selectedDateEpochDay, steps)
    }
}
