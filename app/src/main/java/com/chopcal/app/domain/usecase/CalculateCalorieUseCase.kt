package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.CalorieResult
import com.chopcal.app.domain.model.CookingMethod
import com.chopcal.app.domain.model.Dish
import com.chopcal.app.domain.model.DishCandidate
import com.chopcal.app.domain.model.Portion
import com.chopcal.app.domain.repository.IngredientRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * 热量计算（可解释）：配方（食材 + 调料）× 食材库营养，逐行求和。
 *
 * - 范围：min 用 amount_min、typical 用 amount_typical、max 用 amount_max 分别求和；
 * - 再 × 分量系数 × 做法系数 × 份数。
 * - 油炸吸油（oil_uptake.json）在 M2 补齐（TODO-M2）。
 *
 * 编辑后的动态重算走 [com.chopcal.app.domain.model.DishDraft.nutrients]（浮动范围）。
 */
@Singleton
class CalculateCalorieUseCase @Inject constructor(
    private val expandRecipe: ExpandRecipeUseCase,
    private val ingredientRepository: IngredientRepository,
) {

    operator fun invoke(
        candidate: DishCandidate,
        portion: Portion,
        method: CookingMethod,
        servings: Int,
    ): CalorieResult {
        val dish = candidate.matchedDish
        if (dish == null) {
            return CalorieResult(
                dishName = candidate.name,
                confidence = candidate.confidence,
                portion = portion,
                method = method,
                servings = servings,
                kcalMin = 0,
                kcalMax = 0,
                typicalKcal = 0,
                proteinG = 0,
                carbsG = 0,
                fatG = 0,
                lines = emptyList(),
                explanation = "未匹配到配方库，无法计算热量",
            )
        }

        val factor = portion.factor * method.coefficient * servings.coerceAtLeast(1)
        val lines = expandRecipe(dish)

        return CalorieResult(
            dishName = dish.name,
            confidence = candidate.confidence,
            portion = portion,
            method = method,
            servings = servings,
            kcalMin = (sumKcal(dish, Amount.MIN) * factor).roundToInt(),
            kcalMax = (sumKcal(dish, Amount.MAX) * factor).roundToInt(),
            typicalKcal = (sumKcal(dish, Amount.TYPICAL) * factor).roundToInt(),
            proteinG = (lines.sumOf { it.proteinG } * factor).roundToInt(),
            carbsG = (lines.sumOf { it.carbsG } * factor).roundToInt(),
            fatG = (lines.sumOf { it.fatG } * factor).roundToInt(),
            lines = lines,
            explanation = buildString {
                append("${dish.name} 按标准配方计算")
                append(" · 分量×${portion.factor}")
                append(" · 做法×${method.coefficient}")
                append(" · ${servings} 份")
            },
        )
    }

    private enum class Amount { MIN, TYPICAL, MAX }

    /** 按克数口径（min / typical / max）求和总热量（kcal）。 */
    private fun sumKcal(dish: Dish, amount: Amount): Double {
        val fromIngredients = dish.ingredients.sumOf { ing ->
            val meta = ingredientRepository.find(ing.name) ?: return@sumOf 0.0
            val g = when (amount) {
                Amount.MIN -> ing.amountMin
                Amount.TYPICAL -> ing.amountTypical
                Amount.MAX -> ing.amountMax
            }
            g * meta.kcalPer100g / 100.0
        }
        val fromSeasonings = dish.seasonings.sumOf { s ->
            val meta = ingredientRepository.find(s.name) ?: return@sumOf 0.0
            val g = when (amount) {
                Amount.MIN -> s.amountMin
                Amount.TYPICAL -> s.amountTypical
                Amount.MAX -> s.amountMax
            }
            g * meta.kcalPer100g / 100.0
        }
        return fromIngredients + fromSeasonings
    }
}
