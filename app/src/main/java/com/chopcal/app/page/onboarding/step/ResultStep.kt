package com.chopcal.app.page.onboarding.step

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.component.PlanBreakdown
import com.chopcal.app.core.design.component.ResultHeroCard
import com.chopcal.app.core.design.component.StepActions
import com.chopcal.app.core.design.component.StepHeader
import com.chopcal.app.core.design.theme.InkFaint
import com.chopcal.app.page.onboarding.OnboardingIntent
import com.chopcal.app.page.onboarding.OnboardingUiState

/**
 * Item 7：你的计划（结果页）。拆解默认展开，底部预留隐私合规位。
 */
@Composable
fun ResultStep(state: OnboardingUiState, onIntent: (OnboardingIntent) -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(24.dp))
        StepHeader(
            kicker = stringResource(R.string.result_kicker),
            title = stringResource(R.string.result_title),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            val target = state.dailyTarget
            if (target != null) {
                ResultHeroCard(target = target)
                Spacer(Modifier.height(18.dp))
                PlanBreakdown(target = target)
            }
            Spacer(Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.privacy_placeholder),
                fontSize = 10.sp,
                color = InkFaint,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        StepActions(
            primaryText = stringResource(R.string.result_start_logging),
            onPrimary = { onIntent(OnboardingIntent.Next) },
            trailingArrow = true,
        )
        Spacer(Modifier.height(20.dp))
    }
}
