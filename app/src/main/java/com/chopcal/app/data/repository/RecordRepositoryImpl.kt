package com.chopcal.app.data.repository

import com.chopcal.app.data.db.dao.DailyMetricDao
import com.chopcal.app.data.db.dao.MealRecordDao
import com.chopcal.app.data.db.entity.DailyMetricEntity
import com.chopcal.app.data.db.entity.MealRecordEntity
import com.chopcal.app.domain.model.DailyMetric
import com.chopcal.app.domain.model.MealRecord
import com.chopcal.app.domain.model.MealType
import com.chopcal.app.domain.repository.RecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordRepositoryImpl @Inject constructor(
    private val mealRecordDao: MealRecordDao,
    private val dailyMetricDao: DailyMetricDao,
) : RecordRepository {

    override fun observeMeals(dateEpochDay: Long): Flow<List<MealRecord>> =
        mealRecordDao.observeByDate(dateEpochDay).map { list -> list.map { it.toDomain() } }

    override fun observeMealsRange(startEpochDay: Long, endEpochDay: Long): Flow<List<MealRecord>> =
        mealRecordDao.observeRange(startEpochDay, endEpochDay).map { list -> list.map { it.toDomain() } }

    override fun observeMetric(dateEpochDay: Long): Flow<DailyMetric> =
        dailyMetricDao.observeByDate(dateEpochDay)
            .map { it?.toDomain() ?: DailyMetric(dateEpochDay = dateEpochDay) }

    override fun observeMetricsRange(startEpochDay: Long, endEpochDay: Long): Flow<List<DailyMetric>> =
        dailyMetricDao.observeRange(startEpochDay, endEpochDay)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun saveMeal(record: MealRecord) {
        mealRecordDao.insert(MealRecordEntity.fromDomain(record))
    }

    override suspend fun saveMeals(records: List<MealRecord>) {
        if (records.isEmpty()) return
        mealRecordDao.insertAll(records.map { MealRecordEntity.fromDomain(it) })
    }

    override suspend fun deleteMeal(id: Long) {
        mealRecordDao.byId(id)?.let { mealRecordDao.delete(it) }
    }

    override suspend fun mealsFor(dateEpochDay: Long, mealType: MealType): List<MealRecord> =
        mealRecordDao.byDateAndType(dateEpochDay, mealType.name).map { it.toDomain() }

    override suspend fun deleteMeals(dateEpochDay: Long, mealType: MealType) {
        mealRecordDao.deleteByDateAndType(dateEpochDay, mealType.name)
    }

    override suspend fun setWaterMl(dateEpochDay: Long, ml: Int) {
        dailyMetricDao.upsert(
            DailyMetricEntity(
                dateEpochDay = dateEpochDay,
                waterMl = ml,
                stepCount = dailyMetricDao.getByDate(dateEpochDay)?.stepCount ?: 0,
            )
        )
    }

    override suspend fun setStepCount(dateEpochDay: Long, steps: Int) {
        dailyMetricDao.upsert(
            DailyMetricEntity(
                dateEpochDay = dateEpochDay,
                waterMl = dailyMetricDao.getByDate(dateEpochDay)?.waterMl ?: 0,
                stepCount = steps,
            )
        )
    }
}
