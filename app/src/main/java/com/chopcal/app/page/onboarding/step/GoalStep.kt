package com.chopcal.app.page.onboarding.step

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chopcal.app.R
import com.chopcal.app.core.design.component.SelectableCard
import com.chopcal.app.core.design.component.StepActions
import com.chopcal.app.core.design.component.StepHeader
import com.chopcal.app.domain.model.GoalType
import com.chopcal.app.page.onboarding.OnboardingIntent
import com.chopcal.app.page.onboarding.OnboardingUiState

/**
 * Item 1：目标（四选一）。
 */
@Composable
fun GoalStep(state: OnboardingUiState, onIntent: (OnboardingIntent) -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(24.dp))
        StepHeader(
            kicker = stringResource(R.string.goal_kicker),
            title = stringResource(R.string.goal_title),
            sub = stringResource(R.string.goal_sub),
        )
        Column(
            modifier = Modifier.weight(1f).padding(top = 50.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            GoalType.entries.forEach { goal ->
                SelectableCard(
                    selected = state.draft.goalType == goal,
                    icon = goal.icon(),
                    title = stringResource(goal.labelRes),
                    description = stringResource(goal.descriptionRes),
                    onClick = { onIntent(OnboardingIntent.SelectGoal(goal)) },
                    modifier = Modifier.padding(bottom = 10.dp),
                )
            }
        }
        StepActions(
            primaryText = stringResource(R.string.common_continue),
            primaryEnabled = state.canProceed,
            onPrimary = { onIntent(OnboardingIntent.Next) },
        )
        Spacer(Modifier.height(20.dp))
    }
}

private fun GoalType.icon(): String = when (this) {
    GoalType.LOSE -> "↘"
    GoalType.MAINTAIN -> "◉"
    GoalType.GAIN -> "↗"
    GoalType.MUSCLE -> "✳"
}
