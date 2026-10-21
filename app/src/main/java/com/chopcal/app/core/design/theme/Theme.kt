package com.chopcal.app.core.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.chopcal.app.domain.model.ThemeMode

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

private val ChopCalDarkColorScheme = darkColorScheme(
    primary = DarkChopCalColors.forestGreen,
    onPrimary = DarkChopCalColors.inkOnGreen,
    secondary = DarkChopCalColors.lime,
    onSecondary = DarkChopCalColors.ink,
    background = DarkChopCalColors.paper,
    onBackground = DarkChopCalColors.ink,
    surface = DarkChopCalColors.screen,
    onSurface = DarkChopCalColors.ink,
    surfaceVariant = DarkChopCalColors.surfaceMint,
    onSurfaceVariant = DarkChopCalColors.inkMuted,
    outline = DarkChopCalColors.line,
    error = DarkChopCalColors.warning,
    onError = Color.White,
)

/**
 * ChopCal 统一主题。支持深浅色，通过 [LocalChopCalColors] 提供主题色。
 *
 * 规则：所有 UI 颜色一律从 [chopCalColors] 取，不要写死颜色，以适配深浅色。
 */
@Composable
fun ChopCalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkChopCalColors else LightChopCalColors
    SystemBarsAppearance(darkTheme)
    CompositionLocalProvider(LocalChopCalColors provides colors) {
        MaterialTheme(
            colorScheme = if (darkTheme) ChopCalDarkColorScheme else ChopCalLightColorScheme,
            typography = ChopCalTypography,
            content = content,
        )
    }
}

/**
 * 根据用户选择的主题模式解析是否深色。
 */
@Composable
fun resolveDarkTheme(mode: ThemeMode): Boolean = when (mode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}
