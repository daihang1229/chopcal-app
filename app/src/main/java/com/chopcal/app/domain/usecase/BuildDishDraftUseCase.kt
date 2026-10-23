package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.CookingMethod
import com.chopcal.app.domain.model.DishCandidate
import com.chopcal.app.domain.model.DishDraft
import com.chopcal.app.domain.model.Portion
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 由识别候选构建一道菜草稿（配方展开为可编辑的食材行）。
 *
 * 候选未命中配方库时返回 null（走搜索/拆食材兜底）。
 */
@Singleton
class BuildDishDraftUseCase @Inject constructor(
    private val expandRecipe: ExpandRecipeUseCase,
) {

    operator fun invoke(
        candidate: DishCandidate,
        portion: Portion,
        method: CookingMethod,
        servings: Int,
    ): DishDraft? {
        val dish = candidate.matchedDish ?: return null
        return DishDraft(
            id = System.currentTimeMillis(),
            dishName = dish.name,
            confidence = candidate.confidence,
            method = method,
            lines = expandRecipe(dish),
            portion = portion,
            servings = servings,
        )
    }
}
