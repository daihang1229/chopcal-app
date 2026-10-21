package com.chopcal.app.domain.model

import androidx.annotation.StringRes
import com.chopcal.app.R
import kotlinx.serialization.Serializable

/**
 * 运动量（5 档）。携带 TDEE 活动系数。
 */
@Serializable
enum class ActivityLevel(
    @StringRes val labelRes: Int,
    @StringRes val descriptionRes: Int,
    val factor: Double,
) {
    SEDENTARY(R.string.activity_sedentary, R.string.activity_sedentary_desc, 1.2),
    LIGHT(R.string.activity_light, R.string.activity_light_desc, 1.375),
    MODERATE(R.string.activity_moderate, R.string.activity_moderate_desc, 1.55),
    HIGH(R.string.activity_high, R.string.activity_high_desc, 1.725),
    VERY_HIGH(R.string.activity_very_high, R.string.activity_very_high_desc, 1.9),
}
