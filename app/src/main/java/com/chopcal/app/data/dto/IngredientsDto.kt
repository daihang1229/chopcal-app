package com.chopcal.app.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 食材库（ingredients.json，145 种）。用于拆食材兜底与手动录入。
 */
@Serializable
data class IngredientsDto(
    @SerialName("ingredients") val ingredients: List<IngredientDto> = emptyList(),
)

@Serializable
data class IngredientDto(
    @SerialName("name") val name: String,
    @SerialName("category") val category: String = "",
    @SerialName("kcal_per_100g") val kcalPer100g: Double = 0.0,
    @SerialName("carbs_g") val carbsG: Double = 0.0,
    @SerialName("protein_g") val proteinG: Double = 0.0,
    @SerialName("fat_g") val fatG: Double = 0.0,
    @SerialName("raw_or_cooked") val rawOrCooked: String = "",
)
