package com.chopcal.app.page.onboarding

import com.chopcal.app.domain.model.ActivityLevel
import com.chopcal.app.domain.model.BmiResult
import com.chopcal.app.domain.model.DailyTarget
import com.chopcal.app.domain.model.Gender
import com.chopcal.app.domain.model.GoalType
import com.chopcal.app.domain.model.UserProfile
import com.chopcal.app.page.onboarding.step.StepId
import com.rainbow.compose.core.contract.UiEffect
import com.rainbow.compose.core.contract.UiIntent

/** 引导页 Intent。 */
sealed interface OnboardingIntent : UiIntent {
    data class SelectGoal(val goal: GoalType) : OnboardingIntent
    data class SelectGender(val gender: Gender) : OnboardingIntent
    data class UpdateAge(val age: Int?) : OnboardingIntent
    data class UpdateHeightCm(val cm: Double?) : OnboardingIntent
    data class UpdateWeightKg(val kg: Double?) : OnboardingIntent
    data object ToggleUnit : OnboardingIntent
    data class UpdateTargetWeightKg(val kg: Double?) : OnboardingIntent
    data class SelectTimeline(val weeks: Int?) : OnboardingIntent
    data class SelectActivity(val level: ActivityLevel) : OnboardingIntent
    data object Next : OnboardingIntent
    data object Back : OnboardingIntent
    data object Skip : OnboardingIntent
    data object Finish : OnboardingIntent
    data object OpenPrivacy : OnboardingIntent
    data object RequestNotificationPermission : OnboardingIntent
    data class GoTo(val step: StepId) : OnboardingIntent
    data class PageSettled(val index: Int) : OnboardingIntent
}

/** 引导页一次性副作用。 */
sealed interface OnboardingEffect : UiEffect {
    data object GoHome : OnboardingEffect
    data object RequestNotificationPermission : OnboardingEffect
    data object OpenPrivacy : OnboardingEffect
}

/** 字段错误标识。 */
enum class FieldId {
    AGE,
    HEIGHT,
    WEIGHT,
    TARGET_WEIGHT,
    TIMELINE,
}

/** 引导页 UI 状态。 */
data class OnboardingUiState(
    val initialized: Boolean = false,
    val steps: List<StepId> = emptyList(),
    val currentIndex: Int = 0,
    val draft: UserProfile = UserProfile(),
    val bmi: BmiResult? = null,
    val dailyTarget: DailyTarget? = null,
    val canProceed: Boolean = false,
    val fieldErrors: Map<FieldId, Int> = emptyMap(),
)
