package com.chopcal.app.domain.model

import androidx.annotation.StringRes
import com.chopcal.app.R

/**
 * 拍摄弹窗里的餐次选择目标：四餐之一，或「仅识别」（只查热量、不保存）。
 */
sealed interface CaptureMealTarget {
    data class Meal(val mealType: MealType) : CaptureMealTarget
    data object QueryOnly : CaptureMealTarget
}

/**
 * 录入方式。MVP 只有拍照 + 相册；扫码 + 文字/语音为二期。
 */
enum class InputMethod(@StringRes val labelRes: Int, val mvp: Boolean) {
    CAMERA(R.string.input_camera, true),
    ALBUM(R.string.input_album, true),
    BARCODE(R.string.input_barcode, false),
    TEXT_OR_VOICE(R.string.input_text_voice, false),
}

/**
 * 分量（便捷模式）。系数：小 0.7 / 中 1.0 / 大 1.3。
 */
enum class Portion(@StringRes val labelRes: Int, val factor: Double) {
    SMALL(R.string.portion_small, 0.7),
    MEDIUM(R.string.portion_medium, 1.0),
    LARGE(R.string.portion_large, 1.3),
}

/**
 * 做法。系数对齐「热量计算规则」：少油 0.95 / 正常 1.0 / 油炸 1.3。
 */
enum class CookingMethod(@StringRes val labelRes: Int, val factor: Double) {
    LESS_OIL(R.string.method_less_oil, 0.95),
    NORMAL(R.string.method_normal, 1.0),
    FRIED(R.string.method_fried, 1.3),
}
