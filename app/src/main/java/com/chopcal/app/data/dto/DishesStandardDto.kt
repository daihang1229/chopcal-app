package com.chopcal.app.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 标准配方库（dishes_standard.json）。
 *
 * 克数为「标准参考值范围」（amount_min / amount_max / amount_typical），
 * 热量由配方 × 食材库营养推导，AI 只认菜名 + 拆食材。
 */
@Serializable
data class DishesStandardDto(
    @SerialName("dishes") val dishes: List<DishDto> = emptyList(),
)

@Serializable
data class DishDto(
    @SerialName("name") val name: String,
    @SerialName("category") val category: String = "",
    @SerialName("method") val method: String = "",
    @SerialName("confidence") val confidence: String = "",
    @SerialName("aliases") val aliases: List<AliasDto> = emptyList(),
    @SerialName("ingredients") val ingredients: List<RecipeIngredientDto> = emptyList(),
    @SerialName("seasonings") val seasonings: List<SeasoningDto> = emptyList(),
)

/**
 * 别名（拼音 / 别名 / 俗语俚语 / 外国名）。当前 dishes_standard.json 尚未补齐，解析为空，后续数据侧补充。
 */
@Serializable
data class AliasDto(
    @SerialName("type") val type: String = "",
    @SerialName("value") val value: String = "",
)

@Serializable
data class RecipeIngredientDto(
    @SerialName("name") val name: String,
    @SerialName("role") val role: String = "",
    @SerialName("amount_min") val amountMin: Double = 0.0,
    @SerialName("amount_max") val amountMax: Double = 0.0,
    @SerialName("amount_typical") val amountTypical: Double = 0.0,
)

@Serializable
data class SeasoningDto(
    @SerialName("name") val name: String,
    @SerialName("amount_min") val amountMin: Double = 0.0,
    @SerialName("amount_max") val amountMax: Double = 0.0,
    @SerialName("amount_typical") val amountTypical: Double = 0.0,
)
