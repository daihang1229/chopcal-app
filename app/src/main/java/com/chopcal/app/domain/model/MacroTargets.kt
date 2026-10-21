package com.chopcal.app.domain.model

/**
 * 每日宏量目标。由 [DailyTarget] 推导（HOME.md §4.1）：
 * - Protein = daily_target.protein_g
 * - Fat     = daily_kcal × 25% ÷ 9
 * - Carbs   = (daily_kcal − 蛋白×4 − 脂肪×9) ÷ 4
 */
data class MacroTargets(
    val proteinG: Int = 0,
    val carbsG: Int = 0,
    val fatG: Int = 0,
)
