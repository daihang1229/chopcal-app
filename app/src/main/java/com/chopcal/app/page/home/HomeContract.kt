package com.chopcal.app.page.home

import com.chopcal.app.domain.model.DailySummary
import com.chopcal.app.domain.model.DailyTarget
import com.chopcal.app.domain.model.GoalType
import com.chopcal.app.domain.model.MacroTargets
import com.rainbow.compose.core.contract.UiEffect
import com.rainbow.compose.core.contract.UiIntent

/** 首页 Intent。 */
sealed interface HomeIntent : UiIntent {
    data class SelectDate(val epochDay: Long) : HomeIntent
    data class SetWaterMl(val ml: Int) : HomeIntent
    data class SetStepCount(val steps: Int) : HomeIntent
}

/** 首页一次性副作用（当前无副作用，预留）。 */
sealed interface HomeEffect : UiEffect

/** 周条里的某一天。 */
data class DayItem(
    val epochDay: Long,
    val weekdayLabel: String,
    val dayOfMonth: Int,
    val isToday: Boolean,
    val isFuture: Boolean,
    val isSelected: Boolean,
)

/** 首页 UI 状态（识别流程已拆分到 page/recognize）。 */
data class HomeUiState(
    val loading: Boolean = true,
    val selectedDateEpochDay: Long = 0,
    val todayEpochDay: Long = 0,
    val week: List<DayItem> = emptyList(),
    val dailyTarget: DailyTarget? = null,
    val goalType: GoalType? = null,
    val nickname: String? = null,
    val macroTargets: MacroTargets = MacroTargets(),
    val summary: DailySummary = DailySummary(dateEpochDay = 0),
)
