package com.chopcal.app.domain.usecase

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 营养对照（纯本地除法）：这一餐 ÷ 每日目标。
 */
@Singleton
class NutrientComparatorUseCase @Inject constructor() {

    data class Comparison(
        val percent: Int,
        val remainingKcal: Int,
    )

    /** [thisKcal] 占 [dailyKcal] 的百分比 + 剩余可吃热量。 */
    operator fun invoke(thisKcal: Int, dailyKcal: Int): Comparison {
        val percent = if (dailyKcal > 0) (thisKcal * 100 / dailyKcal).coerceIn(0, 999) else 0
        val remaining = (dailyKcal - thisKcal).coerceAtLeast(0)
        return Comparison(percent, remaining)
    }
}
