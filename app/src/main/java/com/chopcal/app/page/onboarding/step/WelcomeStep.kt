package com.chopcal.app.page.onboarding.step

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.component.KickerText
import com.chopcal.app.core.design.component.PrimaryButton
import com.chopcal.app.core.design.theme.Ink
import com.chopcal.app.core.design.theme.InkMuted
import com.chopcal.app.core.design.theme.Lime
import com.chopcal.app.core.design.theme.TitleStyle
import com.chopcal.app.page.onboarding.OnboardingIntent
import com.chopcal.app.page.onboarding.OnboardingUiState

/**
 * Item 0：品牌/欢迎页。食物大图占位 + 扫描框 + 标语 + CTA。
 * 自动计时跳转由 OnboardingScreen 处理。
 */
@Composable
fun WelcomeStep(
    state: OnboardingUiState,
    onIntent: (OnboardingIntent) -> Unit,
    countdownSeconds: Int,
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF14231B), Color(0xFF275B46)))),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.66f)
                    .height(200.dp)
                    .border(1.dp, Lime, RoundedCornerShape(18.dp)),
            )
            Text(
                text = "AI FOOD SCAN · CHINESE CUISINE",
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.BottomStart).padding(18.dp),
            )
        }
        Spacer(Modifier.height(22.dp))
        KickerText(stringResource(R.string.welcome_kicker))
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.welcome_title),
            style = TitleStyle.copy(fontSize = 31.sp),
            color = Ink,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.welcome_sub),
            fontSize = 13.sp,
            color = InkMuted,
        )
        Spacer(Modifier.height(24.dp))
        PrimaryButton(
            text = stringResource(R.string.welcome_cta),
            onClick = { onIntent(OnboardingIntent.Next) },
            trailingArrow = true,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.welcome_countdown, countdownSeconds),
            fontSize = 12.sp,
            color = InkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.welcome_legal),
            fontSize = 9.sp,
            color = InkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(20.dp))
    }
}
