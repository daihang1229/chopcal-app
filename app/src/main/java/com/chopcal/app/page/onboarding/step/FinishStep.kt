package com.chopcal.app.page.onboarding.step
import com.chopcal.app.core.design.theme.chopCalColors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.component.KickerText
import com.chopcal.app.core.design.component.StepActions
import com.chopcal.app.core.design.theme.TitleStyle
import com.chopcal.app.page.onboarding.OnboardingIntent
import com.chopcal.app.page.onboarding.OnboardingUiState

/**
 * Item 8：收尾（提醒 + 隐私 + 免费额度 + 进入）。登录已砍。
 */
@Composable
fun FinishStep(state: OnboardingUiState, onIntent: (OnboardingIntent) -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(24.dp))
        KickerText(stringResource(R.string.finish_kicker))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FinishVisual()
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.finish_title),
                style = TitleStyle,
                color = chopCalColors.ink,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(9.dp))
            Text(
                text = stringResource(R.string.finish_sub),
                fontSize = 13.sp,
                color = chopCalColors.inkMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(22.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                SettingRow(
                    icon = "♧",
                    title = stringResource(R.string.finish_setting_reminder),
                    sub = stringResource(R.string.finish_setting_reminder_sub),
                    action = stringResource(R.string.finish_action_turn_on),
                )
                SettingRow(
                    icon = "⌑",
                    title = stringResource(R.string.finish_setting_privacy),
                    sub = stringResource(R.string.finish_setting_privacy_sub),
                    action = "›",
                )
            }
        }
        StepActions(
            primaryText = stringResource(R.string.finish_enter),
            onPrimary = { onIntent(OnboardingIntent.Finish) },
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.finish_legal),
            fontSize = 9.sp,
            color = chopCalColors.inkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun FinishVisual() {
    Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .border(1.dp, chopCalColors.forestGreen.copy(alpha = 0.3f), androidx.compose.foundation.shape.CircleShape),
        )
        Box(
            modifier = Modifier
                .size(76.dp)
                .background(chopCalColors.forestGreen, RoundedCornerShape(25.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "✓", fontSize = 33.sp, color = chopCalColors.lime)
        }
    }
}

@Composable
private fun SettingRow(
    icon: String,
    title: String,
    sub: String,
    action: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(33.dp)
                .background(chopCalColors.forestGreen.copy(alpha = 0.08f), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = icon, fontSize = 14.sp, color = chopCalColors.forestGreen)
        }
        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = chopCalColors.ink)
            Text(text = sub, fontSize = 10.sp, color = chopCalColors.inkMuted, modifier = Modifier.padding(top = 3.dp))
        }
        Text(text = action, fontSize = 10.sp, color = chopCalColors.forestGreen)
    }
}
