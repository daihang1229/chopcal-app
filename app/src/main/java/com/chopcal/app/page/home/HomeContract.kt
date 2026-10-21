package com.chopcal.app.page.home

import com.chopcal.app.domain.model.CalorieResult
import com.chopcal.app.domain.model.CaptureMealTarget
import com.chopcal.app.domain.model.CookingMethod
import com.chopcal.app.domain.model.DailySummary
import com.chopcal.app.domain.model.DailyTarget
import com.chopcal.app.domain.model.DishCandidate
import com.chopcal.app.domain.model.GoalType
import com.chopcal.app.domain.model.MacroTargets
import com.chopcal.app.domain.model.MealType
import com.chopcal.app.domain.model.Portion
import com.rainbow.compose.core.contract.UiEffect
import com.rainbow.compose.core.contract.UiIntent

/** 首页 Intent。 */
sealed interface HomeIntent : UiIntent {
    data class SelectDate(val epochDay: Long) : HomeIntent
    data object OpenCapture : HomeIntent
    data class OpenCaptureForMeal(val mealType: MealType) : HomeIntent
    data class SelectMealTarget(val target: CaptureMealTarget) : HomeIntent
    data class StartCapture(val imageUri: String?) : HomeIntent
    data object DismissCapture : HomeIntent
    data object RetryRecognize : HomeIntent
    data object BackToCandidates : HomeIntent
    data class SelectCandidate(val index: Int) : HomeIntent
    data object NotThisDish : HomeIntent
    data class SelectPortion(val portion: Portion) : HomeIntent
    data class SelectCookingMethod(val method: CookingMethod) : HomeIntent
    data class UpdateServings(val servings: Int) : HomeIntent
    data object ComputeResult : HomeIntent
    data object SaveResult : HomeIntent
    data object DismissResult : HomeIntent
    data class SetWaterMl(val ml: Int) : HomeIntent
    data class SetStepCount(val steps: Int) : HomeIntent
}

/** 首页一次性副作用（当前无副作用，预留）。 */
sealed interface HomeEffect : UiEffect

/** 拍摄弹窗阶段。 */
enum class CaptureStage { INPUT, RECOGNIZING, CANDIDATES, ADJUST, RESULT }

/** 周条里的某一天。 */
data class DayItem(
    val epochDay: Long,
    val weekdayLabel: String,
    val dayOfMonth: Int,
    val isToday: Boolean,
    val isFuture: Boolean,
    val isSelected: Boolean,
)

/** 首页 UI 状态。 */
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
    // 拍摄弹窗
    val captureVisible: Boolean = false,
    val captureStage: CaptureStage = CaptureStage.INPUT,
    val captureTarget: CaptureMealTarget = CaptureMealTarget.QueryOnly,
    val candidates: List<DishCandidate> = emptyList(),
    val selectedCandidateIndex: Int = 0,
    val portion: Portion = Portion.MEDIUM,
    val cookingMethod: CookingMethod = CookingMethod.NORMAL,
    val servings: Int = 1,
    val calorieResult: CalorieResult? = null,
) {
    val selectedCandidate: DishCandidate? get() = candidates.getOrNull(selectedCandidateIndex)
}
