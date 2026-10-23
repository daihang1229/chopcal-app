package com.chopcal.app.domain.model

/**
 * 一条饮食记录（一道菜 / 一顿已识别结果）。
 */
data class MealRecord(
    val id: Long = 0,
    /** 所属日期（LocalDate.toEpochDay()）。 */
    val dateEpochDay: Long,
    val mealType: MealType,
    val dishName: String,
    val kcal: Int,
    /** 热量范围（min/max），记录态可回溯。 */
    val kcalMin: Int = 0,
    val kcalMax: Int = 0,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    /** 食材明细 JSON（name + amount_g），记录态展示「这道菜含什么」。 */
    val ingredientsJson: String? = null,
    /** 食物缩略图（记录态四餐圈展示，MVP 可空）。 */
    val thumbnailUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)
