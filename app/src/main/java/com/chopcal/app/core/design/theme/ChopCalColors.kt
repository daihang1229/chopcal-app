package com.chopcal.app.core.design.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * ChopCal 主题色集合。所有组件统一从这里取色，深浅色自动切换。
 *
 * 使用方式（组件内）：
 * ```
 * val colors = chopCalColors
 * Text(color = colors.ink)
 * ```
 */
@Immutable
data class ChopCalColors(
    val paper: Color,        // 页面背景
    val screen: Color,       // 屏幕/弹窗底色
    val card: Color,         // 卡片白
    val surfaceMint: Color,  // 选中态浅绿底
    val surfaceMuted: Color, // 图标浅底
    val ink: Color,          // 文字主色
    val inkMuted: Color,     // 弱化文字
    val inkFaint: Color,     // 提示/占位
    val inkOnGreen: Color,   // 绿底上的文字
    val forestGreen: Color,  // 主色
    val lime: Color,         // 强调
    val line: Color,         // 分割线
    val inputLine: Color,    // 输入框边框
    val warning: Color,      // 警示
    val bmiBlue: Color,      // BMI 偏轻
    val bmiGreen: Color,     // BMI 适中
    val bmiOrange: Color,    // BMI 偏重
)

val LightChopCalColors = ChopCalColors(
    paper = Paper,
    screen = ScreenBg,
    card = CardWhite,
    surfaceMint = SurfaceMint,
    surfaceMuted = SurfaceMuted,
    ink = Ink,
    inkMuted = InkMuted,
    inkFaint = InkFaint,
    inkOnGreen = InkOnGreen,
    forestGreen = ForestGreen,
    lime = Lime,
    line = Line,
    inputLine = InputLine,
    warning = WarningOrange,
    bmiBlue = BmiBlue,
    bmiGreen = BmiGreen,
    bmiOrange = BmiOrange,
)

val DarkChopCalColors = ChopCalColors(
    paper = Color(0xFF121510),
    screen = Color(0xFF1A1E1A),
    card = Color(0xFF20251F),
    surfaceMint = Color(0xFF26312A),
    surfaceMuted = Color(0xFF262B26),
    ink = Color(0xFFE8ECE6),
    inkMuted = Color(0xFF9AA39A),
    inkFaint = Color(0xFF6E756E),
    inkOnGreen = Color(0xFFFFFFFF),
    forestGreen = Color(0xFF618A37),
    lime = Color(0xFFD5D8B2),
    line = Color(0xFF2A302A),
    inputLine = Color(0xFF3A413A),
    warning = Color(0xFFF09A7A),
    bmiBlue = Color(0xFF7BA5F2),
    bmiGreen = Color(0xFFA9C789),
    bmiOrange = Color(0xFFE0A268),
)

val LocalChopCalColors = staticCompositionLocalOf { LightChopCalColors }

/** 便捷访问当前主题色。 */
val chopCalColors: ChopCalColors
    @Composable get() = LocalChopCalColors.current
