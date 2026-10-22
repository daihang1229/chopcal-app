package com.chopcal.app.page.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R

/**
 * 冷启动闪屏：主题 icon + 品牌 slogan，给用户「App 已打开」的视觉反馈。
 *
 * 背景色与 [android.R.attr.windowBackground]（Theme.ChopCal 的 splash_background）一致，
 * 从系统窗口背景到 Compose 闪屏无缝衔接，消除白屏。
 */
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF618A37)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.mipmap.ic_launcher),
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape),
            )
            Spacer(Modifier.height(22.dp))
            Text(
                text = "ChopCal",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                color = Color.White,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "EAT WELL, FEEL WELL",
                fontSize = 12.sp,
                letterSpacing = 1.6.sp,
                color = Color(0xFFD5D8B2),
            )
        }
    }
}
