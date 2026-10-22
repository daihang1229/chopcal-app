package com.chopcal.app.page.onboarding

import androidx.lifecycle.viewModelScope
import com.chopcal.app.core.analytics.AnalyticsEvents
import com.chopcal.app.core.analytics.AnalyticsTracker
import com.chopcal.app.core.analytics.CrashReporter
import com.chopcal.app.domain.model.BmiResult
import com.chopcal.app.domain.model.DefaultProfile
import com.chopcal.app.domain.model.DeviceInfo
import com.chopcal.app.domain.model.LengthUnit
import com.chopcal.app.domain.model.UserProfile
import com.chopcal.app.domain.model.WeightUnit
import com.chopcal.app.domain.repository.UserProfileRepository
import com.chopcal.app.domain.usecase.CalculateBmiUseCase
import com.chopcal.app.domain.usecase.CalculateDailyTargetUseCase
import com.chopcal.app.domain.usecase.validator.AgeValidator
import com.chopcal.app.domain.usecase.validator.HeightValidator
import com.chopcal.app.domain.usecase.validator.TargetWeightValidator
import com.chopcal.app.domain.usecase.validator.TimelineValidator
import com.chopcal.app.domain.usecase.validator.WeightValidator
import com.chopcal.app.page.onboarding.step.OnboardingFlow
import com.chopcal.app.page.onboarding.step.StepId
import com.rainbow.compose.core.contract.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

private const val MAX_SKIP_COUNT = 3

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val profileRepository: UserProfileRepository,
    private val calculateBmi: CalculateBmiUseCase,
    private val calculateDailyTarget: CalculateDailyTargetUseCase,
    private val analyticsTracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
) : BaseMviViewModel<OnboardingIntent, OnboardingEffect>() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val device = profileRepository.ensureDevice()
            analyticsTracker.setUserId(device.uuid)
            crashReporter.setUserId(device.uuid)
            if (!shouldEnterOnboarding(device)) {
                sendEffect(OnboardingEffect.GoHome)
                return@launch
            }
            val draft = profileRepository.profile.first().toDraft()
            val steps = OnboardingFlow.resolve(draft)
            _uiState.value = OnboardingUiState(
                initialized = true,
                steps = steps,
                currentIndex = 0,
                draft = draft,
                bmi = computeBmi(draft),
                canProceed = canProceed(steps.first(), draft),
                fieldErrors = computeErrors(draft),
            )
            analyticsTracker.logEvent(AnalyticsEvents.ONBOARDING_START)
        }
    }

    /**
     * 进入判断：填写过数据 → 首页；否则按跳过规则决定是否进引导页。
     * 跳过次数 < 3 且当天未跳过 → 进引导页；否则直接进首页（用默认数据）。
     */
    private fun shouldEnterOnboarding(device: DeviceInfo): Boolean =
        !device.onboarded &&
            device.skipCount < MAX_SKIP_COUNT &&
            !isSkippedToday(device.lastSkipAt)

    /** 「当天」按本地时间凌晨 3 点切分。 */
    private fun isSkippedToday(lastSkipAt: Long): Boolean {
        if (lastSkipAt <= 0) return false
        val today3am = LocalDate.now()
            .atTime(3, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return lastSkipAt >= today3am
    }

    override suspend fun handleIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.SelectGoal -> updateDraft { it.copy(goalType = intent.goal) }
            is OnboardingIntent.SelectGender -> updateDraft { it.copy(gender = intent.gender) }
            is OnboardingIntent.UpdateAge -> updateDraft { it.copy(age = intent.age) }
            is OnboardingIntent.UpdateHeightCm -> updateDraft { it.copy(heightCm = intent.cm) }
            is OnboardingIntent.UpdateWeightKg -> updateDraft { it.copy(weightKg = intent.kg) }
            is OnboardingIntent.ToggleUnit -> toggleUnit()
            is OnboardingIntent.UpdateTargetWeightKg -> updateDraft { it.copy(targetWeightKg = intent.kg) }
            is OnboardingIntent.SelectTimeline -> updateDraft { it.copy(timelineWeeks = intent.weeks) }
            is OnboardingIntent.SelectActivity -> updateDraft { it.copy(activityLevel = intent.level) }
            is OnboardingIntent.Next -> onNext()
            is OnboardingIntent.Back -> onBack()
            is OnboardingIntent.Skip -> onSkip()
            is OnboardingIntent.Finish -> onFinish()
            OnboardingIntent.OpenPrivacy -> sendEffect(OnboardingEffect.OpenPrivacy)
            OnboardingIntent.RequestNotificationPermission -> sendEffect(OnboardingEffect.RequestNotificationPermission)
            is OnboardingIntent.GoTo -> onGoTo(intent.step)
            is OnboardingIntent.PageSettled -> onPageSettled(intent.index)
        }
    }

    // ── 状态更新 ──────────────────────────────────────────────

    private fun updateDraft(transform: (UserProfile) -> UserProfile) {
        val state = _uiState.value
        val draft = transform(state.draft)
        val steps = OnboardingFlow.resolve(draft)
        val currentStep = steps.getOrNull(state.currentIndex) ?: StepId.WELCOME
        _uiState.value = state.copy(
            draft = draft,
            steps = steps,
            bmi = computeBmi(draft),
            canProceed = canProceed(currentStep, draft),
            fieldErrors = computeErrors(draft),
        )
    }

    private fun toggleUnit() {
        updateDraft {
            it.copy(
                lengthUnit = if (it.lengthUnit == LengthUnit.METRIC) LengthUnit.IMPERIAL else LengthUnit.METRIC,
                weightUnit = if (it.weightUnit == WeightUnit.METRIC) WeightUnit.IMPERIAL else WeightUnit.METRIC,
            )
        }
    }

    // ── 导航 ──────────────────────────────────────────────────

    private suspend fun onNext() {
        val state = _uiState.value
        val step = state.steps.getOrNull(state.currentIndex) ?: return
        profileRepository.saveProfile(state.draft)
        if (step == StepId.RESULT) {
            state.dailyTarget?.let { profileRepository.saveDailyTarget(it) }
        }
        navigateTo(state.currentIndex + 1)
    }

    private fun onBack() {
        // 欢迎页（index 0）不可回退，最低回到目标页（index 1）
        navigateTo((_uiState.value.currentIndex - 1).coerceAtLeast(1))
    }

    private fun onGoTo(step: StepId) {
        val idx = _uiState.value.steps.indexOf(step)
        if (idx >= 0) navigateTo(idx)
    }

    private fun onPageSettled(index: Int) {
        if (index != _uiState.value.currentIndex) navigateTo(index)
    }

    private suspend fun onSkip() {
        val defaults = DefaultProfile.profile
        profileRepository.saveDailyTarget(calculateDailyTarget(defaults))
        profileRepository.recordSkip()
        analyticsTracker.logEvent(AnalyticsEvents.ONBOARDING_SKIP)
        sendEffect(OnboardingEffect.GoHome)
    }

    private suspend fun onFinish() {
        val state = _uiState.value
        profileRepository.saveProfile(state.draft)
        state.dailyTarget?.let { profileRepository.saveDailyTarget(it) }
        profileRepository.markOnboarded()
        analyticsTracker.logEvent(AnalyticsEvents.ONBOARDING_FINISH)
        sendEffect(OnboardingEffect.GoHome)
    }

    private fun navigateTo(index: Int) {
        val state = _uiState.value
        val clamped = index.coerceIn(0, state.steps.lastIndex)
        val step = state.steps[clamped]
        val target = if (step == StepId.RESULT) {
            state.dailyTarget ?: calculateDailyTarget(state.draft.effective())
        } else {
            state.dailyTarget
        }
        if (clamped != state.currentIndex) {
            analyticsTracker.logEvent(
                AnalyticsEvents.ONBOARDING_STEP,
                mapOf(
                    AnalyticsEvents.PARAM_STEP to step.name,
                    AnalyticsEvents.PARAM_INDEX to clamped,
                ),
            )
        }
        _uiState.value = state.copy(
            currentIndex = clamped,
            dailyTarget = target,
            canProceed = canProceed(step, state.draft),
        )
    }

    // ── 校验 ──────────────────────────────────────────────────

    private fun computeBmi(draft: UserProfile): BmiResult? {
        val h = draft.heightCm ?: return null
        val w = draft.weightKg ?: return null
        return calculateBmi(h, w)
    }

    private fun computeErrors(draft: UserProfile): Map<FieldId, Int> {
        val errors = mutableMapOf<FieldId, Int>()
        AgeValidator.validate(draft.age)?.let { errors[FieldId.AGE] = it }
        HeightValidator.validate(draft.heightCm)?.let { errors[FieldId.HEIGHT] = it }
        WeightValidator.validate(draft.weightKg)?.let { errors[FieldId.WEIGHT] = it }
        TargetWeightValidator.validate(draft.goalType, draft.weightKg, draft.targetWeightKg, draft.timelineWeeks)
            ?.let { errors[FieldId.TARGET_WEIGHT] = it }
        TimelineValidator.validate(draft.timelineWeeks)?.let { errors[FieldId.TIMELINE] = it }
        return errors
    }

    private fun canProceed(step: StepId, draft: UserProfile): Boolean = when (step) {
        StepId.WELCOME -> true
        StepId.GOAL -> draft.goalType != null
        StepId.BASIC_INFO -> draft.gender != null && draft.age != null && AgeValidator.validate(draft.age) == null
        StepId.BODY_DATA -> draft.heightCm != null && draft.weightKg != null &&
            HeightValidator.validate(draft.heightCm) == null && WeightValidator.validate(draft.weightKg) == null
        StepId.BMI_FEEDBACK -> true
        StepId.TARGET -> draft.targetWeightKg != null && draft.timelineWeeks != null &&
            TargetWeightValidator.validate(draft.goalType, draft.weightKg, draft.targetWeightKg, draft.timelineWeeks) == null &&
            TimelineValidator.validate(draft.timelineWeeks) == null
        StepId.ACTIVITY -> draft.activityLevel != null
        StepId.RESULT -> true
        StepId.FINISH -> true
    }

    private fun UserProfile?.toDraft(): UserProfile {
        val p = this
        return UserProfile(
            goalType = p?.goalType,
            gender = p?.gender,
            age = p?.age ?: DefaultProfile.profile.age,
            heightCm = p?.heightCm ?: DefaultProfile.profile.heightCm,
            weightKg = p?.weightKg ?: DefaultProfile.profile.weightKg,
            targetWeightKg = p?.targetWeightKg,
            timelineWeeks = p?.timelineWeeks,
            activityLevel = p?.activityLevel,
            lengthUnit = p?.lengthUnit ?: LengthUnit.METRIC,
            weightUnit = p?.weightUnit ?: WeightUnit.METRIC,
        )
    }
}
