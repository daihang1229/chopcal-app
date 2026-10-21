package com.chopcal.app.domain.model

import androidx.annotation.StringRes
import com.chopcal.app.R

/**
 * BMI 分档（中国标准，18.5 / 24.0 为界）。
 * 颜色映射放在 UI 层，领域层保持纯净。
 */
enum class BmiBand(
    @StringRes val labelRes: Int,
    @StringRes val noteRes: Int,
) {
    UNDERWEIGHT(R.string.bmi_underweight, R.string.bmi_underweight_note),
    NORMAL(R.string.bmi_normal, R.string.bmi_normal_note),
    OVERWEIGHT(R.string.bmi_overweight, R.string.bmi_overweight_note),
}

data class BmiResult(
    val value: Double,
    val band: BmiBand,
)
