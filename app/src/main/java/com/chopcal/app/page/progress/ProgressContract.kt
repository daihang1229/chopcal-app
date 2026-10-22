package com.chopcal.app.page.progress

import com.chopcal.app.domain.model.GoalType
import com.chopcal.app.domain.model.OnTrackStatus
import com.chopcal.app.domain.model.WeightUnit
import com.rainbow.compose.core.contract.UiEffect
import com.rainbow.compose.core.contract.UiIntent

/** 体重图时间范围（自开始 / 1月 / 7日）。 */
enum class WeightRange { SINCE_START, ONE_MONTH, SEVEN_DAYS }

/** 卡路里 / 步数饮水图时间范围（7日 / 1月）。 */
enum class ShortRange { SEVEN_DAYS, ONE_MONTH }

/** 进度页 Intent。 */
sealed interface ProgressIntent : UiIntent {
    data class SelectWeightRange(val range: WeightRange) : ProgressIntent
    data class SelectCaloriesRange(val range: ShortRange) : ProgressIntent
    data class SelectStepsWaterRange(val range: ShortRange) : ProgressIntent
    data class RecordWeight(val weightKg: Double, val dateEpochDay: Long) : ProgressIntent
    data object OpenRecordList : ProgressIntent
}

/** 进度页一次性副作用。 */
sealed interface ProgressEffect : UiEffect {
    data object OpenRecordList : ProgressEffect
}

/** 体重图数据点。 */
data class WeightPoint(val epochDay: Long, val weightKg: Double)

/** 卡路里图单柱。 */
data class CalorieBar(
    val epochDay: Long,
    val kcal: Float,
    val weekdayLabel: String,
    val isToday: Boolean,
)

/** 步数/饮水折线点（值统一为 Float）。 */
data class MetricPoint(val epochDay: Long, val value: Float)

/** 目标卡片 UI。 */
data class GoalCardUi(
    val goalType: GoalType = GoalType.MAINTAIN,
    val initialWeightKg: Double = 0.0,
    val currentWeightKg: Double = 0.0,
    val targetWeightKg: Double? = null,
    val changedKg: Double = 0.0,
    val remainingKg: Double = 0.0,
    val projectedDateEpochDay: Long? = null,
    val fluctuationKg: Double? = null,
    val progressFraction: Float = 0f,
)

/** 体重动态图 UI。 */
data class WeightChartUi(
    val range: WeightRange = WeightRange.SINCE_START,
    val points: List<WeightPoint> = emptyList(),
    val targetWeightKg: Double? = null,
    val projectedDateEpochDay: Long? = null,
    val daysRemaining: Long? = null,
    val onTrack: OnTrackStatus = OnTrackStatus.INSUFFICIENT,
    val lateWeeks: Int = 0,
    val hasData: Boolean = false,
)

/** 卡路里摄入图 UI。 */
data class CaloriesChartUi(
    val range: ShortRange = ShortRange.SEVEN_DAYS,
    val bars: List<CalorieBar> = emptyList(),
    val targetKcal: Int = 0,
    val hasData: Boolean = false,
)

/** 步数 & 饮水图 UI。 */
data class StepsWaterChartUi(
    val range: ShortRange = ShortRange.SEVEN_DAYS,
    val steps: List<MetricPoint> = emptyList(),
    val water: List<MetricPoint> = emptyList(),
    val hasData: Boolean = false,
)

/** 本周小结 UI。 */
data class WeeklySummaryUi(
    val avgKcal: Int = 0,
    val deltaKcal: Int = 0,
    val proteinG: Int = 0,
    val carbsG: Int = 0,
    val fatG: Int = 0,
    val metDays: Int = 0,
    val totalDays: Int = 7,
    val streakDays: Int = 0,
    val hasData: Boolean = false,
)

/** 进度页 UI 状态。 */
data class ProgressUiState(
    val loading: Boolean = true,
    val weightUnit: WeightUnit = WeightUnit.METRIC,
    val goalCard: GoalCardUi = GoalCardUi(),
    val weight: WeightChartUi = WeightChartUi(),
    val calories: CaloriesChartUi = CaloriesChartUi(),
    val stepsWater: StepsWaterChartUi = StepsWaterChartUi(),
    val weekly: WeeklySummaryUi = WeeklySummaryUi(),
)
