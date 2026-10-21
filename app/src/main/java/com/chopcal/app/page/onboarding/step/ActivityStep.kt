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
import com.chopcal.app.core.design.component.RadioRow
import com.chopcal.app.core.design.component.StepActions
import com.chopcal.app.core.design.component.StepHeader
import com.chopcal.app.domain.model.ActivityLevel
import com.chopcal.app.page.onboarding.OnboardingIntent
import com.chopcal.app.page.onboarding.OnboardingUiState

/**
 * Item 6：运动量（5 档）。
 */
@Composable
fun ActivityStep(state: OnboardingUiState, onIntent: (OnboardingIntent) -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(24.dp))
        StepHeader(
            kicker = stringResource(R.string.activity_kicker),
            title = stringResource(R.string.activity_title),
            sub = stringResource(R.string.activity_sub),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            ActivityLevel.entries.forEach { level ->
                RadioRow(
                    selected = state.draft.activityLevel == level,
                    title = stringResource(level.labelRes),
                    description = stringResource(level.descriptionRes),
                    trailing = level.factor.toString(),
                    onClick = { onIntent(OnboardingIntent.SelectActivity(level)) },
                    modifier = Modifier.padding(bottom = 9.dp),
                )
            }
        }
        StepActions(
            primaryText = stringResource(R.string.activity_generate_plan),
            primaryEnabled = state.canProceed,
            onPrimary = { onIntent(OnboardingIntent.Next) },
        )
        Spacer(Modifier.height(20.dp))
    }
}
