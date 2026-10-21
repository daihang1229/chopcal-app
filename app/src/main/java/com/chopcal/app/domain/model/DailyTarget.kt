package com.chopcal.app.domain.model

import kotlinx.serialization.Serializable

/**
 * 每日计划结果。同时携带「为什么是这个数字」的拆解行。
 */
@Serializable
data class DailyTarget(
    val dailyKcal: Int,
    val proteinG: Int,
    val projectedDateEpochDay: Long,
    val floorApplied: Boolean,
    // 拆解
    val bmr: Int,
    val tdee: Int,
    val adjustment: Int,
    val activityFactor: Double,
)
