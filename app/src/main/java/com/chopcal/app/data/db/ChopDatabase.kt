package com.chopcal.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chopcal.app.data.db.dao.DailyMetricDao
import com.chopcal.app.data.db.dao.MealRecordDao
import com.chopcal.app.data.db.entity.DailyMetricEntity
import com.chopcal.app.data.db.entity.MealRecordEntity

@Database(
    entities = [MealRecordEntity::class, DailyMetricEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class ChopDatabase : RoomDatabase() {
    abstract fun mealRecordDao(): MealRecordDao
    abstract fun dailyMetricDao(): DailyMetricDao
}
