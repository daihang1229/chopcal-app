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
}
