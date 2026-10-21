package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.CalorieResult
import com.chopcal.app.domain.model.CookingMethod
import com.chopcal.app.domain.model.DishCandidate
import com.chopcal.app.domain.model.Portion
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * 热量计算（便捷模式）：候选基准热量 × 分量系数 × 做法系数 × 份数。
 *
 * 输出「范围」而非精确值（PRD 核心原则：宁给范围诚实，不给精确值失信）。
 */
@Singleton
class CalculateCalorieUseCase @Inject constructor() {

    operator fun invoke(
        candidate: DishCandidate,
        portion: Portion,
        method: CookingMethod,
        servings: Int,
    ): CalorieResult {
        val factor = portion.factor * method.factor * servings.coerceAtLeast(1)

        val typical = (candidate.baseKcal * factor).roundToInt()
        val min = (candidate.baseKcal * factor * 0.8).roundToInt()
        val max = (candidate.baseKcal * factor * 1.35).roundToInt()

        return CalorieResult(
            dishName = candidate.name,
            confidence = candidate.confidence,
            portion = portion,
            method = method,
            servings = servings,
            typicalKcal = typical,
            kcalMin = min,
            kcalMax = max,
            proteinG = (candidate.proteinG * factor).roundToInt(),
            carbsG = (candidate.carbsG * factor).roundToInt(),
            fatG = (candidate.fatG * factor).roundToInt(),
            ingredients = candidate.ingredients.map {
                it.copy(
                    grams = (it.grams * factor).roundToInt(),
                    kcal = (it.kcal * factor).roundToInt(),
                )
            },
            explanation = buildString {
                append("${candidate.name} 按标准配方计算")
                append(" · 分量×${portion.factor}")
                append(" · 做法×${method.factor}")
                append(" · ${servings} 份")
            },
        )
    }
}
