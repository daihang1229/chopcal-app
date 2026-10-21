package com.chopcal.app.domain.model

import kotlinx.serialization.Serializable

/** 体重单位。存储统一用 kg，仅展示时换算。 */
@Serializable
enum class WeightUnit { METRIC, IMPERIAL }
