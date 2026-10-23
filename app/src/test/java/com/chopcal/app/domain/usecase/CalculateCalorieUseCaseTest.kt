package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.CookingMethod
import com.chopcal.app.domain.model.Dish
import com.chopcal.app.domain.model.DishCandidate
import com.chopcal.app.domain.model.Ingredient
import com.chopcal.app.domain.model.Portion
import com.chopcal.app.domain.model.RecipeIngredient
import com.chopcal.app.domain.repository.IngredientRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateCalorieUseCaseTest {

    private class FakeIngredientRepo : IngredientRepository {
        private val map = mapOf(
            "鸡胸肉" to Ingredient("鸡胸肉", "肉蛋", 133.0, 24.0, 0.0, 5.0, "生"),
        )
        override fun all() = map.values.toList()
        override fun find(name: String) = map[name]
        override fun search(query: String) = map.values.filter { it.name.contains(query) }
    }

    private val fakeRepo = FakeIngredientRepo()
    private val useCase = CalculateCalorieUseCase(ExpandRecipeUseCase(fakeRepo), fakeRepo)

    @Test
    fun `sums formula with portion method servings`() {
        val dish = Dish(
            name = "测试鸡胸",
            category = "测试",
            method = "清炒",
            ingredients = listOf(
                RecipeIngredient("鸡胸肉", "主料", amountTypical = 200.0, amountMin = 150.0, amountMax = 250.0),
            ),
            seasonings = emptyList(),
        )
        val candidate = DishCandidate("测试鸡胸", confidence = 100, matchedDish = dish)
        val result = useCase(candidate, Portion.MEDIUM, CookingMethod.STIR_FRY, 1)

        // typical = 200 * 133/100 * 0.95 = 252.7 → 253
        assertEquals(253, result.typicalKcal)
        // min = 150 * 133/100 * 0.95 = 189.5 → 190
        assertEquals(190, result.kcalMin)
        // max = 250 * 133/100 * 0.95 = 315.9 → 316
        assertEquals(316, result.kcalMax)
        // protein = 200 * 24/100 * 0.95 = 45.6 → 46
        assertEquals(46, result.proteinG)
        assertEquals(1, result.lines.size)
    }

    @Test
    fun `unmatched dish returns zero`() {
        val result = useCase(
            DishCandidate("未知菜", confidence = 10, matchedDish = null),
            Portion.MEDIUM,
            CookingMethod.HOME_STYLE,
            1,
        )
        assertEquals(0, result.typicalKcal)
        assertTrue(result.lines.isEmpty())
    }
}
