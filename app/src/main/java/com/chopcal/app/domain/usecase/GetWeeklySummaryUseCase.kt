package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.DailySummary
import com.chopcal.app.domain.model.WeeklySummary
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 本周小结聚合（PROGRESS.md §6）。
 *
 * 纯函数：入参为本周 / 上周的每日汇总 + 每日目标 kcal，输出 [WeeklySummary]。
 *
 * - 日均摄入 = 本周平均 kcal，较上周变化 = 本周均值 − 上周均值
 * - 宏量平均 = 蛋白 / 碳水 / 脂肪日均 g
 * - 达标口径 = 当天摄入在 [目标×0.9, 目标×1.1] 内算达标
 * - 连续记录天数由调用方另行计算（需跨天查询记录）
 */
@Singleton
class GetWeeklySummaryUseCase @Inject constructor() {

    operator fun invoke(
        thisWeek: List<DailySummary>,
        lastWeek: List<DailySummary>,
        targetKcal: Int?,
    ): WeeklySummary {
        val thisAvg = average(thisWeek.map { it.totalKcal })
        val lastAvg = average(lastWeek.map { it.totalKcal })

        val metDays = if (targetKcal != null && targetKcal > 0) {
            val lower = targetKcal * 0.9
            val upper = targetKcal * 1.1
            thisWeek.count { it.totalKcal.toDouble() in lower..upper }
        } else 0

        return WeeklySummary(
            avgKcal = thisAvg,
            deltaKcal = thisAvg - lastAvg,
            proteinG = average(thisWeek.map { it.proteinG }),
            carbsG = average(thisWeek.map { it.carbsG }),
            fatG = average(thisWeek.map { it.fatG }),
            metDays = metDays,
            totalDays = thisWeek.size.coerceAtLeast(1),
        )
    }

    private fun average(values: List<Int>): Int =
        if (values.isEmpty()) 0 else (values.sum().toDouble() / values.size).toInt()
}
