package com.chopcal.app.domain.model

/**
 * 食材库条目（ingredients.json，145 种）。
 *
 * 营养均为「每 100g」口径：kcal / 蛋白 / 碳水 / 脂肪。
 */
data class Ingredient(
    val name: String,
    val category: String,
    val kcalPer100g: Double,
    val proteinGPer100g: Double,
    val carbsGPer100g: Double,
    val fatGPer100g: Double,
    val rawOrCooked: String,
)
