package com.chopcal.app.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 油炸吸油率表（oil_uptake.json）。油炸食物热量 = 生食材热量 + (生重 × 吸油率 × 9 kcal/g)。
 * M2 计算引擎使用，M1 仅建 DTO 预留。
 */
@Serializable
data class OilUptakeDto(
    @SerialName("items") val items: List<OilUptakeItemDto> = emptyList(),
)

@Serializable
data class OilUptakeItemDto(
    @SerialName("name") val name: String = "",
    @SerialName("oil_uptake_pct") val oilUptakePct: Double = 0.0,
    @SerialName("note") val note: String = "",
)
