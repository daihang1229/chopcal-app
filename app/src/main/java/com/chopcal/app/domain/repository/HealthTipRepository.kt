package com.chopcal.app.domain.repository

import com.chopcal.app.domain.model.HealthTip

/**
 * 健康小提示（health_tips.json），识别中本地滚动展示。
 */
interface HealthTipRepository {

    fun all(): List<HealthTip>

    fun byCategory(category: String): List<HealthTip>
}
