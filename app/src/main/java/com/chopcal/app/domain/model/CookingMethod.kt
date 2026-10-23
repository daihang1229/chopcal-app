package com.chopcal.app.domain.model

/**
 * 烹饪做法（对应 cooking_methods.json 的系数，以「家常炒」为基准 1.0）。
 *
 * 覆盖菜品库实际出现的 16 种做法，另补「烘焙 / 焗」（菜品库出现但 cooking_methods.json 未收录，
 * 系数暂定，需数据侧回填确认）。做法越多，对菜品热量的刻画越细。
 */
enum class CookingMethod(val label: String, val coefficient: Double) {
    BLANCH("白灼", 0.85),
    COLD_DRESS("凉拌", 0.85),
    STEAM("蒸", 0.90),
    BOIL("煮", 0.90),
    STEW("炖", 0.95),
    STIR_FRY("清炒", 0.95),
    HOME_STYLE("家常炒", 1.00),
    BRAISE("焖", 1.00),
    FRESH_MADE("现制", 1.00),
    AU_GRATIN("焗", 1.00),
    RED_BRAISE("红烧", 1.10),
    SWEET_SOUR("糖醋", 1.15),
    PANCAKE("烙", 1.15),
    BAKED("烘焙", 1.15),
    PAN_FRY("煎", 1.15),
    DRY_FRY("干煸", 1.20),
    DEEP_FRY("油炸", 1.30);

    companion object {
        /** 默认做法（基准系数 1.0）。 */
        val DEFAULT: CookingMethod = HOME_STYLE

        /** 由菜品库的 method 字符串解析，未知做法回退到默认。 */
        fun fromLabel(label: String?): CookingMethod =
            entries.firstOrNull { it.label == label } ?: DEFAULT
    }
}
