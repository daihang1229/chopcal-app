package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.Dish
import com.chopcal.app.domain.model.RecipeLine
import com.chopcal.app.domain.repository.IngredientRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 把菜品配方展开为「计算行」：食材 + 调料 × 食材库营养（typical 克数口径）。
 *
 * 食材库缺营养的食材跳过。
 */
@Singleton
class ExpandRecipeUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository,
) {

    operator fun invoke(dish: Dish): List<RecipeLine> {
        val ingredientLines = dish.ingredients.mapNotNull { ing ->
            val meta = ingredientRepository.find(ing.name) ?: return@mapNotNull null
            RecipeLine(
                name = ing.name,
                role = ing.role,
                amountG = ing.amountTypical,
                kcalPer100g = meta.kcalPer100g,
                proteinGPer100g = meta.proteinGPer100g,
                carbsGPer100g = meta.carbsGPer100g,
                fatGPer100g = meta.fatGPer100g,
            )
        }
        val seasoningLines = dish.seasonings.mapNotNull { s ->
            val meta = ingredientRepository.find(s.name) ?: return@mapNotNull null
            RecipeLine(
                name = s.name,
                role = "调料",
                amountG = s.amountTypical,
                kcalPer100g = meta.kcalPer100g,
                proteinGPer100g = meta.proteinGPer100g,
                carbsGPer100g = meta.carbsGPer100g,
                fatGPer100g = meta.fatGPer100g,
            )
        }
        return ingredientLines + seasoningLines
    }
}
