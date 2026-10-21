package com.chopcal.app.page.onboarding.step
import com.chopcal.app.core.design.theme.chopCalColors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.component.BmiRing
import com.chopcal.app.core.design.component.StepActions
import com.chopcal.app.core.design.component.StepHeader
import com.chopcal.app.page.onboarding.OnboardingIntent
import com.chopcal.app.page.onboarding.OnboardingUiState

/**
 * Item 4：身体区间反馈（只读）。
 */
@Composable
fun BmiFeedbackStep(state: OnboardingUiState, onIntent: (OnboardingIntent) -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(24.dp))
        StepHeader(
            kicker = stringResource(R.string.bmi_kicker),
            title = stringResource(R.string.bmi_title),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val bmi = state.bmi
            if (bmi != null) {
                BmiRing(bmi = bmi.value, band = bmi.band)
                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(bmi.band.noteRes),
                    fontSize = 13.sp,
                    lineHeight = 22.sp,
                    color = chopCalColors.inkMuted,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(25.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(chopCalColors.surfaceMuted, RoundedCornerShape(12.dp))
                    .padding(13.dp),
            ) {
                Text(text = "ⓘ", fontSize = 10.sp, color = chopCalColors.inkMuted)
                Spacer(Modifier.size(9.dp))
                Text(
                    text = stringResource(R.string.bmi_info),
                    fontSize = 10.sp,
                    lineHeight = 16.sp,
                    color = chopCalColors.inkMuted,
                )
            }
        }
        StepActions(
            primaryText = stringResource(R.string.common_continue),
            onPrimary = { onIntent(OnboardingIntent.Next) },
        )
        Spacer(Modifier.height(20.dp))
    }
}
