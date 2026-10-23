package com.chopcal.app.data.datasource.local

import com.chopcal.app.data.dto.DishesStandardDto
import com.chopcal.app.data.dto.DishDto
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 菜品库 JSON 数据源（assets/data/dishes_standard.json）。
 */
@Singleton
class DishJsonDataSource @Inject constructor(
    private val contentProvider: JsonContentProvider,
    private val json: Json,
) {

    fun load(): List<DishDto> =
        json.decodeFromString<DishesStandardDto>(contentProvider.read("data/dishes_standard.json")).dishes
}
