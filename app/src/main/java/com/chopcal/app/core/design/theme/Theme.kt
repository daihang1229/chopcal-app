package com.chopcal.app.core.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ChopCalLightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = InkOnGreen,
    secondary = Lime,
    onSecondary = Ink,
    background = Paper,
    onBackground = Ink,
    surface = ScreenBg,
    onSurface = Ink,
    surfaceVariant = SurfaceMint,
    onSurfaceVariant = InkMuted,
    outline = Line,
    error = WarningOrange,
    onError = Color.White,
)

/**
 * ChopCal 统一主题。
 *
 * 当前只做浅色（对齐引导页设计稿），深色后续补充。
 */
@Composable
fun ChopCalTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = ChopCalLightColorScheme,
        typography = ChopCalTypography,
        content = content,
    )
}
