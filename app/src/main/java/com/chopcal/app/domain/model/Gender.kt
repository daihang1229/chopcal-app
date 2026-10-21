package com.chopcal.app.domain.model

import androidx.annotation.StringRes
import com.chopcal.app.R
import kotlinx.serialization.Serializable

/**
 * 生理性别。携带 BMR 系数偏移与热量地板。
 */
@Serializable
enum class Gender(
    @StringRes val labelRes: Int,
    val bmrOffset: Int,
    val floorKcal: Int,
) {
    MALE(R.string.gender_male, 5, 1500),
    FEMALE(R.string.gender_female, -161, 1200),
    OTHER(R.string.gender_other, -78, 1300),
}
