package com.chopcal.app.domain.model

/**
 * 本周小结（PROGRESS.md §6）。
 *
 * @param deltaKcal 较上周日均摄入变化（负 = 减少）
 * @param metDays   本周达标天数（摄入在目标 ±10% 内）
 * @param totalDays 统计天数（默认 7）
 * @param streakDays 连续记录天数（当天有任一识别记录即算）
 */
data class WeeklySummary(
    val avgKcal: Int = 0,
    val deltaKcal: Int = 0,
    val proteinG: Int = 0,
    val carbsG: Int = 0,
    val fatG: Int = 0,
    val metDays: Int = 0,
    val totalDays: Int = 7,
    val streakDays: Int = 0,
) {
    val hasData: Boolean get() = avgKcal > 0
}
