package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.DailyTarget
import com.chopcal.app.domain.model.UserProfile
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * 每日卡路里目标计算（唯一计算入口），严格对齐 ONBOARDING.md §9 / S-1。
 *
 * BMR   = 10×体重 + 6.25×身高 − 5×年龄 + 性别系数
 * TDEE  = BMR × 活动系数
 * 缺口/盈余 = (目标−当前)×方向×7700 ÷ (周数×7)，再按目标类型封顶
 * 地板 = 性别地板，低于地板则封顶 floorApplied=true
 * 蛋白 = 当前体重 × 蛋白系数，取整到 5 的倍数
 */
@Singleton
class CalculateDailyTargetUseCase @Inject constructor() {

    operator fun invoke(profile: UserProfile): DailyTarget {
        val p = profile.effective()

        val bmr = 10 * p.weightKg!! + 6.25 * p.heightCm!! - 5 * p.age!! + p.gender!!.bmrOffset
        val tdee = bmr * p.activityLevel!!.factor

        val adjustment = when (p.goalType!!.direction) {
            0 -> 0.0
            else -> {
                val target = p.targetWeightKg ?: p.weightKg!!
                val weeks = (p.timelineWeeks ?: 0).coerceAtLeast(1)
                val change = (target - p.weightKg!!) * p.goalType!!.direction * 7700.0 / (weeks * 7)
                val capped = min(change, p.goalType!!.calorieCap.toDouble())
                capped * p.goalType!!.direction
            }
        }

        val raw = tdee + adjustment
        val floor = p.gender!!.floorKcal.toDouble()
        val kcal = max(floor, raw).roundToInt()

        val protein = ((p.weightKg!! * p.goalType!!.proteinFactor) / 5).roundToInt() * 5
        val weeks = p.timelineWeeks ?: 0
        val projected = LocalDate.now().plusWeeks(weeks.toLong()).toEpochDay()

        return DailyTarget(
            dailyKcal = kcal,
            proteinG = protein,
            projectedDateEpochDay = projected,
            floorApplied = raw < floor,
            bmr = bmr.roundToInt(),
            tdee = tdee.roundToInt(),
            adjustment = adjustment.roundToInt(),
            activityFactor = p.activityLevel!!.factor,
        )
    }
}
