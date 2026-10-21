package com.chopcal.app.domain.model

import androidx.annotation.StringRes
import com.chopcal.app.R

/**
 * 餐次。HOME.md §9 枚举：breakfast / lunch / snack / dinner（query_only 见 [CaptureMealTarget]）。
 *
 * 附带「按当地时间默认选中」的时间窗口规则（HOME.md §7.1）：
 * 5–10 早餐 / 10–14:30 午餐 / 14:30–17 加餐 / 17–22 晚餐 / 其他 加餐。
 */
enum class MealType(@StringRes val labelRes: Int) {
    BREAKFAST(R.string.meal_breakfast),
    LUNCH(R.string.meal_lunch),
    SNACK(R.string.meal_snack),
    DINNER(R.string.meal_dinner);

    companion object {
        /** 按当地时间小时/分钟解析默认餐次。 */
        fun fromTime(hour: Int, minute: Int): MealType {
            val t = hour * 60 + minute
            return when {
                t in (5 * 60) until (10 * 60) -> BREAKFAST
                t in (10 * 60) until (14 * 60 + 30) -> LUNCH
                t in (14 * 60 + 30) until (17 * 60) -> SNACK
                t in (17 * 60) until (22 * 60) -> DINNER
                else -> SNACK
            }
        }
    }
}
