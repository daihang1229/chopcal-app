package com.chopcal.app.domain.model

/**
 * 菜品库里的标准菜（成熟菜品，标准配方）。
 *
 * [ingredients] 为「食材 + 克数 + 该食材热量」明细，用于可解释性展示。
 */
data class Dish(
    val name: String,
    /** 标准份（中份/正常做法）典型热量。 */
    val baseKcal: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    val ingredients: List<IngredientLine> = emptyList(),
)

/**
 * 一道菜的食材明细行。
 */
data class IngredientLine(
    val name: String,
    val grams: Int,
    val kcal: Int,
)

/**
 * 识别候选（AI 从菜品库候选里选，带置信度）。
 */
data class DishCandidate(
    val name: String,
    /** 置信度 0–100。 */
    val confidence: Int,
    val baseKcal: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    val ingredients: List<IngredientLine> = emptyList(),
)
