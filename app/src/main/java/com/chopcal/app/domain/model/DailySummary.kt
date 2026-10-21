package com.chopcal.app.domain.model

/**
 * 单餐汇总（四餐圈的记录态数据）。
 */
data class MealSummary(
    val kcal: Int = 0,
    val thumbnailUri: String? = null,
)

/**
 * 某日摄入汇总：总热量 + 宏量 + 各餐 + 水/步。
 */
data class DailySummary(
    val dateEpochDay: Long,
    val totalKcal: Int = 0,
    val proteinG: Int = 0,
    val carbsG: Int = 0,
    val fatG: Int = 0,
    val perMeal: Map<MealType, MealSummary> = emptyMap(),
    val waterMl: Int = 0,
    val stepCount: Int = 0,
) {
    fun meal(mealType: MealType): MealSummary = perMeal[mealType] ?: MealSummary()
}
