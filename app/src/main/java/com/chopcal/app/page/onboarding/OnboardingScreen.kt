package com.chopcal.app.page.onboarding
import com.chopcal.app.core.design.theme.chopCalColors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chopcal.app.R
import com.chopcal.app.core.design.component.ChopConfirmDialog
import com.chopcal.app.core.design.component.ChopLoadingIndicator
import com.chopcal.app.core.design.component.OnboardingProgressBar
import com.chopcal.app.core.design.component.OnboardingTopBar
import com.chopcal.app.page.onboarding.step.ActivityStep
import com.chopcal.app.page.onboarding.step.BasicInfoStep
import com.chopcal.app.page.onboarding.step.BmiFeedbackStep
import com.chopcal.app.page.onboarding.step.BodyDataStep
import com.chopcal.app.page.onboarding.step.FinishStep
import com.chopcal.app.page.onboarding.step.GoalStep
import com.chopcal.app.page.onboarding.step.ResultStep
import com.chopcal.app.page.onboarding.step.StepId
import com.chopcal.app.page.onboarding.step.TargetStep
import com.chopcal.app.page.onboarding.step.WelcomeStep
import com.rainbow.compose.core.ui.CollectEffect
import kotlinx.coroutines.delay

private const val WELCOME_COUNTDOWN_SECONDS = 3

/**
 * 引导页宿主：顶部栏 + 进度条 + HorizontalPager（滑动已禁用，靠按钮前进/后退）。
 */
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onGoHome: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            OnboardingEffect.GoHome -> onGoHome()
            OnboardingEffect.RequestNotificationPermission -> Unit
            OnboardingEffect.OpenPrivacy -> Unit
        }
    }

    if (!state.initialized) {
        Box(Modifier.fillMaxSize().background(chopCalColors.paper)) {
            ChopLoadingIndicator()
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { state.steps.size })

    // VM → Pager：按钮导航时滚动到目标页
    LaunchedEffect(state.currentIndex) {
        if (pagerState.currentPage != state.currentIndex) {
            pagerState.animateScrollToPage(state.currentIndex)
        }
    }

    // Pager → VM：滑动已禁用，此处保留以备将来恢复滑动时同步
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.sendIntent(OnboardingIntent.PageSettled(page))
        }
    }

    // 欢迎页倒计时（点击 CTA 会改变 currentIndex，从而取消计时）
    val currentStep = state.steps.getOrNull(state.currentIndex)
    val welcomeCountdown = remember { mutableIntStateOf(WELCOME_COUNTDOWN_SECONDS) }
    LaunchedEffect(state.currentIndex) {
        if (currentStep == StepId.WELCOME) {
            welcomeCountdown.intValue = WELCOME_COUNTDOWN_SECONDS
            while (welcomeCountdown.intValue > 0) {
                delay(1000L)
                welcomeCountdown.intValue--
            }
            viewModel.sendIntent(OnboardingIntent.Next)
        }
    }

    // 跳过确认弹窗
    var showSkipDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(chopCalColors.paper)) {
        OnboardingTopBar(
            actionLabel = topActionLabel(currentStep)?.let { stringResource(it) },
            onAction = {
                when (currentStep) {
                    StepId.GOAL -> showSkipDialog = true
                    else -> viewModel.sendIntent(OnboardingIntent.Back)
                }
            },
        )
        if (currentStep != StepId.WELCOME && currentStep != StepId.FINISH) {
            val progress = state.currentIndex.toFloat() /
                (state.steps.size - 1).coerceAtLeast(1)
            OnboardingProgressBar(progress = progress)
        }
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier.weight(1f),
        ) { page ->
            val step = state.steps.getOrNull(page)
            if (step != null) {
                StepContent(
                    step = step,
                    state = state,
                    countdownSeconds = welcomeCountdown.intValue,
                    onIntent = viewModel::sendIntent,
                )
            }
        }
    }

    if (showSkipDialog) {
        ChopConfirmDialog(
            title = stringResource(R.string.skip_dialog_title),
            message = stringResource(R.string.skip_dialog_message),
            confirmText = stringResource(R.string.common_skip),
            cancelText = stringResource(R.string.common_cancel),
            onConfirm = {
                showSkipDialog = false
                viewModel.sendIntent(OnboardingIntent.Skip)
            },
            onDismiss = { showSkipDialog = false },
        )
    }
}

@Composable
private fun StepContent(
    step: StepId,
    state: OnboardingUiState,
    countdownSeconds: Int,
    onIntent: (OnboardingIntent) -> Unit,
) {
    when (step) {
        StepId.WELCOME -> WelcomeStep(state, onIntent, countdownSeconds)
        StepId.GOAL -> GoalStep(state, onIntent)
        StepId.BASIC_INFO -> BasicInfoStep(state, onIntent)
        StepId.BODY_DATA -> BodyDataStep(state, onIntent)
        StepId.BMI_FEEDBACK -> BmiFeedbackStep(state, onIntent)
        StepId.TARGET -> TargetStep(state, onIntent)
        StepId.ACTIVITY -> ActivityStep(state, onIntent)
        StepId.RESULT -> ResultStep(state, onIntent)
        StepId.FINISH -> FinishStep(state, onIntent)
    }
}

private fun topActionLabel(step: StepId?): Int? = when (step) {
    StepId.GOAL -> R.string.common_skip
    StepId.BASIC_INFO, StepId.BODY_DATA, StepId.BMI_FEEDBACK, StepId.TARGET,
    StepId.ACTIVITY -> R.string.common_back
    else -> null
}
