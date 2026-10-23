package com.chopcal.app.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 健康小提示（health_tips.json，24 条，本地存储，识别中滚动展示）。
 */
@Serializable
data class HealthTipsDto(
    @SerialName("tips") val tips: List<HealthTipDto> = emptyList(),
)

@Serializable
data class HealthTipDto(
    @SerialName("id") val id: Int = 0,
    @SerialName("category") val category: String = "",
    @SerialName("text") val text: String = "",
)
