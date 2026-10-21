package com.chopcal.app.domain.usecase.validator

import com.chopcal.app.R

/**
 * 体重校验（30 ~ 300 kg）。
 */
object WeightValidator {
    const val MIN_KG = 30.0
    const val MAX_KG = 300.0

    fun validate(kg: Double?): Int? = when {
        kg == null -> null
        kg < MIN_KG || kg > MAX_KG -> R.string.error_weight
        else -> null
    }
}
