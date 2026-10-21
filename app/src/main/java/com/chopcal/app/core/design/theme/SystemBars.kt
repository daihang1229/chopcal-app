package com.chopcal.app.core.design.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * 根据主题深浅色自适应系统栏图标颜色（状态栏 + 导航栏）。
 *
 * 浅色主题 → 深色图标（深字）；深色主题 → 浅色图标（白字）。
 * 在 [ChopCalTheme] 内部调用，所有页面自动生效。
 */
@Composable
fun SystemBarsAppearance(darkTheme: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
}
