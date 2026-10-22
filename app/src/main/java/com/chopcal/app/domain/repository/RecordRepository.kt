package com.chopcal.app.domain.repository

import com.chopcal.app.domain.model.DailyMetric
import com.chopcal.app.domain.model.MealRecord
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

    suspend fun deleteMeal(id: Long)

    suspend fun setWaterMl(dateEpochDay: Long, ml: Int)

    suspend fun setStepCount(dateEpochDay: Long, steps: Int)
}
