package com.chopcal.app.domain.repository

import com.chopcal.app.domain.model.DailyMetric
import com.chopcal.app.domain.model.MealRecord
import com.chopcal.app.domain.model.MealType
import kotlinx.coroutines.flow.Flow

/**
 * 饮食记录仓储：MealRecord（Room）+ 水/步（Room）。
 */
interface RecordRepository {

    /** 观察某日全部饮食记录。 */
    fun observeMeals(dateEpochDay: Long): Flow<List<MealRecord>>

    /** 观察某日期区间 [start, end]（含端点）的全部饮食记录。 */
    fun observeMealsRange(startEpochDay: Long, endEpochDay: Long): Flow<List<MealRecord>>

    /** 观察某日的水/步。 */
    fun observeMetric(dateEpochDay: Long): Flow<DailyMetric>

    /** 观察某日期区间 [start, end]（含端点）的水/步。 */
    fun observeMetricsRange(startEpochDay: Long, endEpochDay: Long): Flow<List<DailyMetric>>

    suspend fun saveMeal(record: MealRecord)

    /** 批量保存（一餐多菜）。 */
    suspend fun saveMeals(records: List<MealRecord>)

    suspend fun deleteMeal(id: Long)

    /** 某日某餐已有的记录（用于「添加/替换」判断）。 */
    suspend fun mealsFor(dateEpochDay: Long, mealType: MealType): List<MealRecord>

    /** 删除某日某餐的全部记录（「替换」语义）。 */
    suspend fun deleteMeals(dateEpochDay: Long, mealType: MealType)

    suspend fun setWaterMl(dateEpochDay: Long, ml: Int)

    suspend fun setStepCount(dateEpochDay: Long, steps: Int)
}
