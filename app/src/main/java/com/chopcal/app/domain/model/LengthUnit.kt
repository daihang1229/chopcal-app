package com.chopcal.app.domain.model

import kotlinx.serialization.Serializable

/** 长度单位。存储统一用 cm，仅展示时换算。 */
@Serializable
enum class LengthUnit { METRIC, IMPERIAL }
