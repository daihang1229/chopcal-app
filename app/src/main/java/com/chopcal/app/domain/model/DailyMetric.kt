package com.chopcal.app.domain.model

/**
 * 某日手动记录项：饮水 + 步数（无目标，纯手动）。
 */
data class DailyMetric(
    val dateEpochDay: Long,
    val waterMl: Int = 0,
    val stepCount: Int = 0,
)
