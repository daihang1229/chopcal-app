package com.chopcal.app.domain.model

/**
 * 健康小提示（health_tips.json）。识别中滚动展示，5s 切一条，随机起播。
 */
data class HealthTip(
    val id: Int,
    val category: String,   // ingredient / diet / exercise
    val text: String,
)
