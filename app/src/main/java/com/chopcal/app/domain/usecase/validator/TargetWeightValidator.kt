package com.chopcal.app.domain.usecase.validator

import com.chopcal.app.R
import com.chopcal.app.domain.model.GoalType

/**
 * 目标体重校验：方向一致性 + 速率安全（ONBOARDING.md §7）。
 */
object TargetWeightValidator {

    fun validate(goal: GoalType?, current: Double?, target: Double?, weeks: Int?): Int? {
        if (goal == null || current == null || target == null) return null

        when {
            goal.direction < 0 && target >= current -> return R.string.error_target_lower
            goal.direction > 0 && target <= current -> return R.string.error_target_higher
        }

        if (weeks == null || weeks <= 0) return null

        val weeklyChange = (target - current) * goal.direction / weeks
        val maxWeekly = when {
            goal.direction < 0 -> current * 0.01   // 减重 ≤1% 体重/周
            goal.direction > 0 -> current * 0.005  // 增重 ≤0.5% 体重/周
            else -> return null
        }

        return if (weeklyChange > maxWeekly) {
            if (goal.direction < 0) R.string.error_rate_too_fast_lose
            else R.string.error_rate_too_fast_gain
        } else null
    }
}
