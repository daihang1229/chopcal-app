package com.chopcal.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ChopCal 主入口 Activity。
 *
 * 目前是项目骨架阶段：仅展示品牌占位页，Compose MVI 基础库接入后再替换为正式页面。
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChopCalTheme {
                WelcomeScreen()
            }
        }
    }
}

/** ChopCal 主题：品牌绿色（与 App icon 一致） */
@Composable
fun ChopCalTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = BrandGreen,
        onPrimary = Color.White,
        secondary = BrandOrange,
    )
    MaterialTheme(colorScheme = colorScheme, content = content)
}

/** 品牌色 */
val BrandGreen = Color(0xFF16A34A)
val BrandOrange = Color(0xFFF59E0B)

/** 骨架占位页：品牌名 + Slogan */
@Composable
fun WelcomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "ChopCal",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = BrandGreen,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Know every bite",
            fontSize = 18.sp,
            color = Color.Gray,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    ChopCalTheme {
        WelcomeScreen()
    }
}
