package com.chopcal.app.domain.model

/**
 * 已展开的「计算行」：配方行（食材/调料）× 食材库营养，用于明细展示与动态重算。
 */
data class RecipeLine(
    val name: String,
    val role: String,               // 主料 / 辅料 / 主食 / 调料
    val amountG: Double,            // 用户可改克数
    val kcalPer100g: Double,
    val proteinGPer100g: Double,
    val carbsGPer100g: Double,
    val fatGPer100g: Double,
) {
    val kcal: Double get() = amountG * kcalPer100g / 100.0
    val proteinG: Double get() = amountG * proteinGPer100g / 100.0
    val carbsG: Double get() = amountG * carbsGPer100g / 100.0
    val fatG: Double get() = amountG * fatGPer100g / 100.0
}

/**
 * 一道菜的营养合计（范围 + 三大营养素）。
 */
data class NutrientTotal(
    val kcalMin: Int = 0,
    val kcalTypical: Int = 0,
    val kcalMax: Int = 0,
    val proteinG: Int = 0,
    val carbsG: Int = 0,
    val fatG: Int = 0,
)
