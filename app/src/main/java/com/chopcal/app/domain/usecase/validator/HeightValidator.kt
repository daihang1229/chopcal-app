package com.chopcal.app.domain.usecase.validator

import com.chopcal.app.R

/**
 * 身高校验（100 ~ 250 cm）。
 */
object HeightValidator {
    const val MIN_CM = 100.0
    const val MAX_CM = 250.0

    fun validate(cm: Double?): Int? = when {
        cm == null -> null
        cm < MIN_CM || cm > MAX_CM -> R.string.error_height
        else -> null
    }
}
