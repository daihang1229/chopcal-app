package com.chopcal.app.domain.model

/**
 * 别名（拼音 / 别名 / 俗语俚语 / 外国名），用于模糊搜索。
 */
data class Alias(
    val type: String,   // pinyin / alias / foreign / slang
    val value: String,
)

/**
 * 菜品配方行（食材 + 克数范围，单位 g）。
 */
data class RecipeIngredient(
    val name: String,
    val role: String,               // 主料 / 辅料 / 主食
    val amountTypical: Double,
    val amountMin: Double,
    val amountMax: Double,
)

/**
 * 调料行（只算油糖等，克数范围）。
 */
data class Seasoning(
    val name: String,
    val amountTypical: Double,
    val amountMin: Double,
    val amountMax: Double,
)

/**
 * 菜品库里的标准菜（成熟菜品，标准配方）。
 *
 * 热量/营养由 [ingredients] + [seasonings] 配方 × 食材库营养推导，可解释。
 */
data class Dish(
    val name: String,
    val category: String,
    val method: String,
    val aliases: List<Alias> = emptyList(),
    val ingredients: List<RecipeIngredient> = emptyList(),
    val seasonings: List<Seasoning> = emptyList(),
) {
    /** 对应的做法（未知回退默认家常炒）。 */
    val cookingMethod: CookingMethod get() = CookingMethod.fromLabel(method)
}

/**
 * 识别候选（AI 从菜品库候选里选，带置信度）。
 *
 * [matchedDish] 为候选菜名匹配到的配方库菜品；命中则直接用配方算热量，不自由发挥。
 * [aiIngredients] 为 AI 拆的食材（候选都不对时的拆食材兜底用）。
 */
data class DishCandidate(
    val name: String,
    val nameEn: String? = null,
    val confidence: Int,
    val matchedDish: Dish? = null,
    val aiIngredients: List<String> = emptyList(),
)
