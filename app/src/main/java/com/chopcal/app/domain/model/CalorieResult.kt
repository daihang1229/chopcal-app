package com.chopcal.app.domain.model

/**
 * 热量计算结果（范围 + 明细 + 可解释性）。
 */
data class CalorieResult(
    val dishName: String,
    val confidence: Int,
    val portion: Portion,
    val method: CookingMethod,
    val servings: Int,
    /** 热量范围，宁给范围不给精确值。 */
    val kcalMin: Int,
    val kcalMax: Int,
    val typicalKcal: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    /** 食材明细（含每行营养），用于可解释性展示。 */
    val lines: List<RecipeLine>,
    /** 可解释性文案：为什么是这个热量。 */
    val explanation: String,
)
