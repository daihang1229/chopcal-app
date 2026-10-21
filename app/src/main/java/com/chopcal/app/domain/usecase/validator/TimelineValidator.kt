package com.chopcal.app.domain.usecase.validator

import com.chopcal.app.R

/**
 * 达成时间校验（1 ~ 104 周）。
 */
object TimelineValidator {
    const val MIN = 1
    const val MAX = 104

    fun validate(weeks: Int?): Int? = when {
        weeks == null -> null
        weeks !in MIN..MAX -> R.string.error_timeline_range
        else -> null
    }
}
