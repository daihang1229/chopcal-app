package com.chopcal.app.data.repository

import com.chopcal.app.data.datasource.local.HealthTipJsonDataSource
import com.chopcal.app.data.dto.HealthTipDto
import com.chopcal.app.domain.model.HealthTip
import com.chopcal.app.domain.repository.HealthTipRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 健康小提示 JSON 实现（health_tips.json，24 条）。
 */
@Singleton
class HealthTipRepositoryImpl @Inject constructor(
    private val dataSource: HealthTipJsonDataSource,
) : HealthTipRepository {

    private val tips: List<HealthTip> = dataSource.load().map { it.toDomain() }

    override fun all(): List<HealthTip> = tips

    override fun byCategory(category: String): List<HealthTip> =
        tips.filter { it.category == category }

    private fun HealthTipDto.toDomain(): HealthTip = HealthTip(
        id = id,
        category = category,
        text = text,
    )
}
