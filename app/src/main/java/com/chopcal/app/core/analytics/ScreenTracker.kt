package com.chopcal.app.core.analytics

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 页面级埋点：进入页面记 [AnalyticsEvents.SCREEN_VIEW]，离开时记 [AnalyticsEvents.SCREEN_DURATION]。
 *
 * 由 [com.chopcal.app.core.design.component.TrackedScreen] 在 Compose 进入/离开组合时调用。
 */
@Singleton
class ScreenTracker @Inject constructor(
    private val tracker: AnalyticsTracker,
) {

    /** 进入页面：上报 screen_view。 */
    fun trackScreen(name: String, screenClass: String) {
        tracker.logEvent(
            AnalyticsEvents.SCREEN_VIEW,
            mapOf(
                AnalyticsEvents.PARAM_SCREEN to name,
                AnalyticsEvents.PARAM_SCREEN_CLASS to screenClass,
            ),
        )
    }

    /** 离开页面：上报驻留时长（毫秒）。 */
    fun trackDuration(name: String, durationMs: Long) {
        tracker.logEvent(
            AnalyticsEvents.SCREEN_DURATION,
            mapOf(
                AnalyticsEvents.PARAM_SCREEN to name,
                AnalyticsEvents.PARAM_DURATION_MS to durationMs,
            ),
        )
    }
}
