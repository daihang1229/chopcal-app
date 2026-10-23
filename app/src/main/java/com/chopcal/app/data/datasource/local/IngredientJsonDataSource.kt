package com.chopcal.app.data.datasource.local

import com.chopcal.app.data.dto.IngredientDto
import com.chopcal.app.data.dto.IngredientsDto
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 食材库 JSON 数据源（assets/data/ingredients.json，145 种）。
 */
@Singleton
class IngredientJsonDataSource @Inject constructor(
    private val contentProvider: JsonContentProvider,
    private val json: Json,
) {

    fun load(): List<IngredientDto> =
        json.decodeFromString<IngredientsDto>(contentProvider.read("data/ingredients.json")).ingredients
}
