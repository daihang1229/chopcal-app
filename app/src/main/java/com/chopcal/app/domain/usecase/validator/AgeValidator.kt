package com.chopcal.app.domain.usecase.validator

import com.chopcal.app.R

/**
 * 年龄校验（13 ~ 100 整数）。
 *
 * @return 错误文案的字符串资源 id，null 表示无错误（含未填写，未填写由「下一步」禁用处理）
 */
object AgeValidator {
    const val MIN = 13
    const val MAX = 100

    fun validate(age: Int?): Int? = when {
        age == null -> null
        age !in MIN..MAX -> R.string.error_age_range
        else -> null
    }
}
