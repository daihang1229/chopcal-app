package com.chopcal.app.page.progress

import androidx.lifecycle.viewModelScope
import com.chopcal.app.domain.model.DailySummary
import com.chopcal.app.domain.model.DailyTarget
import com.chopcal.app.domain.model.UserProfile
import com.chopcal.app.domain.model.WeightEntry
import com.chopcal.app.domain.model.WeightUnit
import com.chopcal.app.domain.repository.RecordRepository
import com.chopcal.app.domain.repository.UserProfileRepository
import com.chopcal.app.domain.repository.WeightRepository
import com.chopcal.app.domain.usecase.GetDailySummariesUseCase
import com.chopcal.app.domain.usecase.GetWeeklySummaryUseCase
import com.chopcal.app.domain.usecase.GetWeightProgressUseCase
import com.chopcal.app.domain.usecase.RecordWeightUseCase
import com.rainbow.compose.core.contract.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val profileRepository: UserProfileRepository,
    private val recordRepository: RecordRepository,
    private val weightRepository: WeightRepository,
    private val getWeightProgress: GetWeightProgressUseCase,
    private val getDailySummaries: GetDailySummariesUseCase,
    private val getWeeklySummary: GetWeeklySummaryUseCase,
    private val recordWeight: RecordWeightUseCase,
) : BaseMviViewModel<ProgressIntent, ProgressEffect>() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    private val weightRange = MutableStateFlow(WeightRange.SINCE_START)
    private val caloriesRange = MutableStateFlow(ShortRange.SEVEN_DAYS)
    private val stepsWaterRange = MutableStateFlow(ShortRange.SEVEN_DAYS)

    private var profile: UserProfile? = null
    private val dailyTarget = MutableStateFlow<DailyTarget?>(null)

    private var weights: List<WeightEntry> = emptyList()
    private var calorieSummaries: List<DailySummary> = emptyList()

    init {
        // 档案（响应单位 / 目标变化，改变单位时数值+单位一起重算）
        viewModelScope.launch {
            profileRepository.profile.collect { p ->
                profile = p
                _uiState.update {
                    it.copy(
                        loading = false,
                        weightUnit = p?.weightUnit ?: WeightUnit.METRIC,
                    )
                }
                recomputeWeight()
            }
        }

        // 每日目标
        viewModelScope.launch {
            profileRepository.dailyTarget.collect { t ->
                dailyTarget.value = t
                recomputeWeight()
                updateCalories(calorieSummaries)
            }
        }

        // 称重记录 → 目标卡 + 体重图
        viewModelScope.launch {
            weightRepository.observeWeights().collect { entries ->
                weights = entries
                recomputeWeight()
            }
        }

        // 卡路里图（独立时间范围）
        viewModelScope.launch {
            caloriesRange.collectLatest { range ->
                val window = range.window(LocalDate.now())
                getDailySummaries(window.first, window.last).collect { summaries ->
                    calorieSummaries = summaries
                    updateCalories(summaries)
                }
            }
        }

        // 步数 & 饮水图（独立时间范围）
        viewModelScope.launch {
            stepsWaterRange.collectLatest { range ->
                val window = range.window(LocalDate.now())
                getDailySummaries(window.first, window.last).collect { summaries ->
                    updateStepsWater(summaries)
                }
            }
        }

        // 本周小结（固定本周 + 上周窗口）
        viewModelScope.launch {
            val today = LocalDate.now()
            val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            combine(
                dailyTarget,
                getDailySummaries(monday.toEpochDay(), monday.plusDays(6).toEpochDay()),
                getDailySummaries(monday.minusDays(7).toEpochDay(), monday.minusDays(1).toEpochDay()),
            ) { target, thisWeek, lastWeek ->
                getWeeklySummary(thisWeek, lastWeek, target?.dailyKcal)
            }.collect { summary ->
                _uiState.update { it.copy(weekly = summary.toUi()) }
            }
        }

        // 连续记录天数（近一年内逐日回推）
        viewModelScope.launch {
            val today = LocalDate.now()
            recordRepository.observeMealsRange(today.minusDays(365).toEpochDay(), today.toEpochDay())
                .collect { meals ->
                    val streak = computeStreak(meals.map { it.dateEpochDay }.toSet(), today)
                    _uiState.update { it.copy(weekly = it.weekly.copy(streakDays = streak)) }
                }
        }
    }

    override suspend fun handleIntent(intent: ProgressIntent) {
        when (intent) {
            is ProgressIntent.SelectWeightRange -> onSelectWeightRange(intent.range)
            is ProgressIntent.SelectCaloriesRange -> onSelectCaloriesRange(intent.range)
            is ProgressIntent.SelectStepsWaterRange -> onSelectStepsWaterRange(intent.range)
            is ProgressIntent.RecordWeight -> recordWeight(intent.dateEpochDay, intent.weightKg)
            ProgressIntent.OpenRecordList -> sendEffect(ProgressEffect.OpenRecordList)
        }
    }

    // ── 体重区块 ──────────────────────────────────────────────

    private fun recomputeWeight() {
        val progress = getWeightProgress(profile, dailyTarget.value, weights)
        val today = LocalDate.now()
        _uiState.update { state ->
            state.copy(
                goalCard = progress.toGoalCardUi(),
                weight = state.weight.copy(
                    targetWeightKg = progress.targetWeightKg,
                    projectedDateEpochDay = progress.projectedDateEpochDay,
                    daysRemaining = progress.daysRemaining,
                    onTrack = progress.onTrack,
                    lateWeeks = progress.lateWeeks,
                    points = filterPoints(progress.entries, weightRange.value, today),
                    hasData = progress.entries.isNotEmpty(),
                ),
            )
        }
    }

    private fun onSelectWeightRange(range: WeightRange) {
        weightRange.value = range
        val today = LocalDate.now()
        _uiState.update { state ->
            state.copy(
                weight = state.weight.copy(
                    range = range,
                    points = filterPoints(weights, range, today),
                ),
            )
        }
    }

    // ── 短期图表（独立控制） ─────────────────────────────────

    private fun onSelectCaloriesRange(range: ShortRange) {
        caloriesRange.value = range
        _uiState.update { it.copy(calories = it.calories.copy(range = range)) }
    }

    private fun onSelectStepsWaterRange(range: ShortRange) {
        stepsWaterRange.value = range
        _uiState.update { it.copy(stepsWater = it.stepsWater.copy(range = range)) }
    }

    private fun updateCalories(summaries: List<DailySummary>) {
        val today = LocalDate.now()
        val todayEpochDay = today.toEpochDay()
        _uiState.update { state ->
            state.copy(
                calories = state.calories.copy(
                    bars = summaries.map { s ->
                        val date = LocalDate.ofEpochDay(s.dateEpochDay)
                        CalorieBar(
                            epochDay = s.dateEpochDay,
                            kcal = s.totalKcal.toFloat(),
                            weekdayLabel = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                            isToday = s.dateEpochDay == todayEpochDay,
                        )
                    },
                    targetKcal = dailyTarget.value?.dailyKcal ?: 0,
                    hasData = summaries.any { it.totalKcal > 0 },
                ),
            )
        }
    }

    private fun updateStepsWater(summaries: List<DailySummary>) {
        _uiState.update { state ->
            state.copy(
                stepsWater = state.stepsWater.copy(
                    steps = summaries.map { MetricPoint(it.dateEpochDay, it.stepCount.toFloat()) },
                    water = summaries.map { MetricPoint(it.dateEpochDay, it.waterMl.toFloat()) },
                    hasData = summaries.any { it.stepCount > 0 || it.waterMl > 0 },
                ),
            )
        }
    }

    // ── 工具 ──────────────────────────────────────────────────

    private fun filterPoints(
        entries: List<WeightEntry>,
        range: WeightRange,
        today: LocalDate,
    ): List<WeightPoint> {
        val window = range.window(today)
        val filtered = if (window == null) entries else entries.filter { it.dateEpochDay in window }
        return filtered.map { WeightPoint(it.dateEpochDay, it.weightKg) }
    }

    private fun computeStreak(recordedDays: Set<Long>, today: LocalDate): Int {
        var cursor = today
        // 今天尚未记录时从昨天起算，避免因“当天还没记”而中断连续
        if (cursor.toEpochDay() !in recordedDays) cursor = cursor.minusDays(1)
        var streak = 0
        while (cursor.toEpochDay() in recordedDays) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }
}

// ── 映射 / 窗口扩展 ──────────────────────────────────────────

private fun com.chopcal.app.domain.model.WeightProgress.toGoalCardUi() = GoalCardUi(
    goalType = goalType,
    initialWeightKg = initialWeightKg,
    currentWeightKg = currentWeightKg,
    targetWeightKg = targetWeightKg,
    changedKg = changedKg,
    remainingKg = remainingKg,
    projectedDateEpochDay = projectedDateEpochDay,
    fluctuationKg = fluctuationKg,
    progressFraction = progressFraction,
)

private fun com.chopcal.app.domain.model.WeeklySummary.toUi() = WeeklySummaryUi(
    avgKcal = avgKcal,
    deltaKcal = deltaKcal,
    proteinG = proteinG,
    carbsG = carbsG,
    fatG = fatG,
    metDays = metDays,
    totalDays = totalDays,
    streakDays = streakDays,
    hasData = hasData,
)

private fun WeightRange.window(today: LocalDate): LongRange? = when (this) {
    WeightRange.SINCE_START -> null
    WeightRange.ONE_MONTH -> today.minusDays(29).toEpochDay()..today.toEpochDay()
    WeightRange.SEVEN_DAYS -> today.minusDays(6).toEpochDay()..today.toEpochDay()
}

private fun ShortRange.window(today: LocalDate): LongRange = when (this) {
    ShortRange.SEVEN_DAYS -> today.minusDays(6).toEpochDay()..today.toEpochDay()
    ShortRange.ONE_MONTH -> today.minusDays(29).toEpochDay()..today.toEpochDay()
}
