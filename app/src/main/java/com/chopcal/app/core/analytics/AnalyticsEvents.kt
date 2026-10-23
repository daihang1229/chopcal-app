package com.chopcal.app.core.analytics

/**
 * 埋点事件名与参数 key 常量，集中管理避免拼写错误。
 */
object AnalyticsEvents {

    // Onboarding
    const val ONBOARDING_START = "onboarding_start"
    const val ONBOARDING_STEP = "onboarding_step"
    const val ONBOARDING_FINISH = "onboarding_finish"
    const val ONBOARDING_SKIP = "onboarding_skip"

    const val PARAM_STEP = "step"
    const val PARAM_INDEX = "index"

    // 页面级埋点（screen_view 为 Firebase 标准事件名，firebase_screen* 为保留参数，
    // 会上报进 Firebase 控制台「页面和屏幕」报表）
    const val SCREEN_VIEW = "screen_view"
    const val SCREEN_DURATION = "screen_duration"
    const val PARAM_SCREEN = "firebase_screen"
    const val PARAM_SCREEN_CLASS = "firebase_screen_class"
    const val PARAM_DURATION_MS = "duration_ms"

    /** 宿主 Activity 名（firebase_screen_class）。 */
    const val SCREEN_CLASS_MAIN = "MainActivity"
    const val SCREEN_CLASS_ONBOARDING = "OnboardingActivity"

    /** 页面名常量。 */
    object Screen {
        const val SPLASH = "splash"
        const val ONBOARDING = "onboarding"
        const val HOME = "home"
        const val RECORD = "record"
        const val PROFILE = "profile"
        const val PRO = "pro"
        const val PROGRESS = "progress"
        const val RECOGNIZE = "recognize"
    }
}
