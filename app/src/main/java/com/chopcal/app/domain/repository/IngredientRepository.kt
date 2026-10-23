package com.chopcal.app.domain.repository

import com.chopcal.app.domain.model.Ingredient

/**
 * 食材库（ingredients.json，145 种），用于拆食材兜底与手动录入。
 */
interface IngredientRepository {

    fun all(): List<Ingredient>

    fun find(name: String): Ingredient?

    fun search(query: String): List<Ingredient>
}
