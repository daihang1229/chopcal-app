package com.chopcal.app.domain.model

import kotlin.math.roundToInt

/**
 * 一顿饭里的一道菜草稿（多菜容器单元）。
 *
 * [lines] 为可编辑的「计算行」（食材+调料），改克数/删/加后调用 [nutrients] 即时重算。
 */
data class DishDraft(
    val id: Long,
    val dishName: String,
    val confidence: Int?,
    val method: CookingMethod,
    val lines: List<RecipeLine>,
    val portion: Portion,
    val servings: Int,
) {
    /**
     * 动态重算营养合计：Σ(lines 当前克数 × 每100g营养 ÷ 100) × 分量系数 × 做法系数 × 份数。
     *
     * 范围用 ±15% 浮动（编辑后配方克数范围失效，改为按当前克数浮动）。
     */
    fun nutrients(): NutrientTotal {
        val factor = portion.factor * method.coefficient * servings.coerceAtLeast(1)
        val kcal = lines.sumOf { it.kcal } * factor
        return NutrientTotal(
            kcalMin = (kcal * 0.85).roundToInt(),
            kcalTypical = kcal.roundToInt(),
            kcalMax = (kcal * 1.15).roundToInt(),
            proteinG = (lines.sumOf { it.proteinG } * factor).roundToInt(),
            carbsG = (lines.sumOf { it.carbsG } * factor).roundToInt(),
            fatG = (lines.sumOf { it.fatG } * factor).roundToInt(),
        )
    }

    /** 修改某行克数。 */
    fun withLineAmount(index: Int, amountG: Double): DishDraft =
        copy(lines = lines.mapIndexed { i, line ->
            if (i == index) line.copy(amountG = amountG) else line
        })

    /** 删除某行。 */
    fun withoutLine(index: Int): DishDraft =
        copy(lines = lines.filterIndexed { i, _ -> i != index })

    /** 追加一行（从食材库添加食材）。 */
    fun withLine(line: RecipeLine): DishDraft =
        copy(lines = lines + line)
}
