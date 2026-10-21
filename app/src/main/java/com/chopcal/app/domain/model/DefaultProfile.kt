package com.chopcal.app.domain.model

/**
 * 默认档案（ONBOARDING.md G-3 跳过兜底值）。
 */
object DefaultProfile {
    val profile = UserProfile(
        goalType = GoalType.MAINTAIN,
        gender = Gender.OTHER,
        age = 30,
        heightCm = 170.0,
        weightKg = 65.0,
        activityLevel = ActivityLevel.LIGHT,
    )
}
