package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.CookingMethod
import com.chopcal.app.domain.model.Dish
import com.chopcal.app.domain.model.DishCandidate
import com.chopcal.app.domain.model.Ingredient
import com.chopcal.app.domain.model.Portion
import com.chopcal.app.domain.model.RecipeIngredient
import com.chopcal.app.domain.model.RecipeLine
import com.chopcal.app.domain.repository.IngredientRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class DishDraftTest {

    private class FakeIngredientRepo : IngredientRepository {
        private val map = mapOf(
            "鸡胸肉" to Ingredient("鸡胸肉", "肉蛋", 133.0, 24.0, 0.0, 5.0, "生"),
            "花生" to Ingredient("花生", "坚果", 567.0, 25.8, 16.1, 49.2, "生"),
        )
        override fun all() = map.values.toList()
        override fun find(name: String) = map[name]
        override fun search(query: String) = map.values.filter { it.name.contains(query) }
    }

    private val fakeRepo = FakeIngredientRepo()
    private val buildDraft = BuildDishDraftUseCase(ExpandRecipeUseCase(fakeRepo))

    @Test
    fun `build draft from candidate`() {
        val dish = Dish(
            name = "宫保鸡丁", category = "炒菜", method = "家常炒",
            ingredients = listOf(
                RecipeIngredient("鸡胸肉", "主料", 200.0, 150.0, 250.0),
                RecipeIngredient("花生", "辅料", 50.0, 40.0, 60.0),
            ),
        )
        val candidate = DishCandidate("宫保鸡丁", confidence = 82, matchedDish = dish)
        val draft = buildDraft(candidate, Portion.MEDIUM, CookingMethod.HOME_STYLE, 1)!!
        assertEquals("宫保鸡丁", draft.dishName)
        assertEquals(2, draft.lines.size)
    }

    @Test
    fun `nutrients recompute after edit`() {
        val dish = Dish(
            name = "测试鸡胸", category = "测试", method = "清炒",
            ingredients = listOf(RecipeIngredient("鸡胸肉", "主料", 200.0, 150.0, 250.0)),
        )
        val candidate = DishCandidate("测试鸡胸", confidence = 100, matchedDish = dish)
        val draft = buildDraft(candidate, Portion.MEDIUM, CookingMethod.STIR_FRY, 1)!!

        // typical = 200 * 133/100 * 0.95 = 252.7 → 253
        assertEquals(253, draft.nutrients().kcalTypical)

        // 改克数 200 → 100：100 * 133/100 * 0.95 = 126.35 → 126
        assertEquals(126, draft.withLineAmount(0, 100.0).nutrients().kcalTypical)

        // 删除 → 0
        assertEquals(0, draft.withoutLine(0).nutrients().kcalTypical)
    }

    @Test
    fun `add line recompute`() {
        val dish = Dish(
            name = "测试鸡胸", category = "测试", method = "清炒",
            ingredients = listOf(RecipeIngredient("鸡胸肉", "主料", 200.0, 150.0, 250.0)),
        )
        val candidate = DishCandidate("测试鸡胸", confidence = 100, matchedDish = dish)
        val draft = buildDraft(candidate, Portion.MEDIUM, CookingMethod.STIR_FRY, 1)!!
        val added = draft.withLine(RecipeLine("花生", "辅料", 50.0, 567.0, 25.8, 16.1, 49.2))

        // 253 + 50 * 567/100 * 0.95 = 253 + 269.3 = 522.3 → 522
        assertEquals(522, added.nutrients().kcalTypical)
    }

    @Test
    fun `unmatched candidate returns null`() {
        val candidate = DishCandidate("未知菜", confidence = 10, matchedDish = null)
        assertEquals(null, buildDraft(candidate, Portion.MEDIUM, CookingMethod.HOME_STYLE, 1))
    }
}
