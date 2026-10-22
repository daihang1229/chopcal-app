package com.chopcal.app.page.onboarding.step

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.component.KickerText
import com.chopcal.app.core.design.component.PrimaryButton
import com.chopcal.app.core.design.theme.TitleStyle
import com.chopcal.app.core.design.theme.chopCalColors
import com.chopcal.app.page.onboarding.OnboardingIntent
import com.chopcal.app.page.onboarding.OnboardingUiState

/**
 * Item 0：品牌/欢迎页。扫描框（食物图 + 上下扫描动效） + 标语 + CTA。
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
                .background(Brush.verticalGradient(listOf(Color(0xFF233B17), Color(0xFF618A37)))),
        ) {
            // 扫描框
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.66f)
                    .height(200.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, chopCalColors.lime, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center,
            ) {
                // 示例食物图（宫保鸡丁）
                Image(
                    painter = painterResource(R.drawable.sample_food),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
                // 上下扫描动效（覆盖在食物图上，占满扫描框）
                ScanOverlay(modifier = Modifier.matchParentSize())
            }
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
            color = chopCalColors.ink,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.welcome_sub),
            fontSize = 13.sp,
            color = chopCalColors.inkMuted,
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
            color = chopCalColors.inkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.welcome_legal),
            fontSize = 9.sp,
            color = chopCalColors.inkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(20.dp))
    }
}

/**
 * 上下扫描动效：一条白色扫描线 + 鼠尾草绿光晕，在框内上下往返，横贯整框。
 */
@Composable
private fun ScanOverlay(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "scan")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scanProgress",
    )

    BoxWithConstraints(modifier = modifier) {
        val lineH = 3.dp
        val glowH = 44.dp
        val travel = (maxHeight - lineH).coerceAtLeast(0.dp)
        val lineY = travel * progress

        // 光晕：以扫描线为中心，上下对称渐变
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(glowH)
                .offset(y = lineY - glowH / 2 + lineH / 2)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, chopCalColors.lime.copy(alpha = 0.30f), Color.Transparent),
                    ),
                ),
        )

        // 扫描线
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(lineH)
                .offset(y = lineY)
                .background(Color.White.copy(alpha = 0.92f)),
        )
    }
}
