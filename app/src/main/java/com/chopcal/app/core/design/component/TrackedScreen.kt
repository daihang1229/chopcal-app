package com.chopcal.app.core.design.component

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.chopcal.app.core.analytics.ScreenTracker

/**
 * 页面级埋点包装器。
 *
 * 进入组合时上报 `screen_view`，离开组合（切页/关闭）时上报 `screen_duration`。
 * 用法：把某个页面的根 Composable 包一层即可，无需改动页面内部逻辑。
 *
 * ```
 * TrackedScreen(screenName, screenClass, screenTracker) {
 *     HomeScreen(...)
 * }
 * ```
 *
 * @param screenName 页面名（对应 AnalyticsEvents.Screen.*）
 * @param screenClass 宿主 Activity 名（firebase_screen_class）
 */
@Composable
fun TrackedScreen(
    screenName: String,
    screenClass: String,
    screenTracker: ScreenTracker,
    content: @Composable () -> Unit,
) {
    DisposableEffect(screenName, screenClass, screenTracker) {
        val entry = SystemClock.elapsedRealtime()
        screenTracker.trackScreen(screenName, screenClass)
        onDispose {
            screenTracker.trackDuration(screenName, SystemClock.elapsedRealtime() - entry)
        }
    }
    content()
}
