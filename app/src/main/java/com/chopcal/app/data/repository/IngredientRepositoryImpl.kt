package com.chopcal.app.data.repository

import com.chopcal.app.data.datasource.local.IngredientJsonDataSource
import com.chopcal.app.data.dto.IngredientDto
import com.chopcal.app.domain.model.Ingredient
import com.chopcal.app.domain.repository.IngredientRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 食材库 JSON 实现（ingredients.json，145 种）。
 */
@Singleton
class IngredientRepositoryImpl @Inject constructor(
    private val dataSource: IngredientJsonDataSource,
) : IngredientRepository {

    private val byName: Map<String, Ingredient> = dataSource.load()
        .map { it.toDomain() }
        .associateBy { it.name }

    override fun all(): List<Ingredient> = byName.values.toList()

    override fun find(name: String): Ingredient? = byName[name]

    override fun search(query: String): List<Ingredient> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        return all()
            .mapNotNull { ingredient ->
                val name = ingredient.name.lowercase()
                when {
                    name == q -> ingredient to 100
                    name.contains(q) -> ingredient to 50
                    else -> null
                }
            }
            .sortedByDescending { it.second }
            .map { it.first }
    }

    private fun IngredientDto.toDomain(): Ingredient = Ingredient(
        name = name,
        category = category,
        kcalPer100g = kcalPer100g,
        proteinGPer100g = proteinG,
        carbsGPer100g = carbsG,
        fatGPer100g = fatG,
        rawOrCooked = rawOrCooked,
    )
}
