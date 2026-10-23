package com.chopcal.app.data.datasource.local

import com.chopcal.app.data.dto.HealthTipDto
import com.chopcal.app.data.dto.HealthTipsDto
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 健康小提示 JSON 数据源（assets/data/health_tips.json）。
 */
@Singleton
class HealthTipJsonDataSource @Inject constructor(
    private val contentProvider: JsonContentProvider,
    private val json: Json,
) {

    fun load(): List<HealthTipDto> =
        json.decodeFromString<HealthTipsDto>(contentProvider.read("data/health_tips.json")).tips
}
