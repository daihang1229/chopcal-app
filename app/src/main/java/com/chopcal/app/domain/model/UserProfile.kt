package com.chopcal.app.domain.model

import kotlinx.serialization.Serializable

/**
 * 用户档案 —— 引导页全部字段的「总数据结构」。
 *
 * 内存里的表单草稿、DataStore 持久化、计算入参都用这一个结构。
 * 数值字段（年龄/身高/体重）默认预填；枚举字段（目标/性别/运动量）默认不预选（null）。
 */
@Serializable
data class UserProfile(
    val goalType: GoalType? = null,
    val gender: Gender? = null,
    val age: Int? = null,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val targetWeightKg: Double? = null,
    val timelineWeeks: Int? = null,
    val activityLevel: ActivityLevel? = null,
    val lengthUnit: LengthUnit = LengthUnit.METRIC,
    val weightUnit: WeightUnit = WeightUnit.METRIC,
) {
    /** 用默认值补齐缺失字段，用于计算（跳过的兜底路径也走这里）。 */
    fun effective(): UserProfile = copy(
        goalType = goalType ?: GoalType.MAINTAIN,
        gender = gender ?: Gender.OTHER,
        age = age ?: 30,
        heightCm = heightCm ?: 170.0,
        weightKg = weightKg ?: 65.0,
        activityLevel = activityLevel ?: ActivityLevel.LIGHT,
    )
}
