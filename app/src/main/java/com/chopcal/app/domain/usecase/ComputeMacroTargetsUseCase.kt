package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.MacroTargets
import com.chopcal.app.domain.model.DailyTarget
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * 由每日卡路里目标推导宏量目标（HOME.md §4.1）。
 */
@Singleton
class ComputeMacroTargetsUseCase @Inject constructor() {

    operator fun invoke(target: DailyTarget): MacroTargets {
        val fat = (target.dailyKcal * 0.25 / 9.0).roundToInt()
        val carbs = ((target.dailyKcal - target.proteinG * 4 - fat * 9) / 4.0)
            .roundToInt()
            .coerceAtLeast(0)
        return MacroTargets(
            proteinG = target.proteinG,
            carbsG = carbs,
            fatG = fat,
        )
    }
}
