package com.chopcal.app.domain.model

/**
 * 一条称重记录（date + weight_kg），对应 PROGRESS.md §7 `weight_entries[]`。
 *
 * 存储统一用 kg，仅展示时按 [WeightUnit] 换算。
 */
data class WeightEntry(
    val dateEpochDay: Long,
    val weightKg: Double,
)
