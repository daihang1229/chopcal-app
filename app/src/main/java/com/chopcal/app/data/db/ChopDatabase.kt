package com.chopcal.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chopcal.app.data.db.dao.DailyMetricDao
import com.chopcal.app.data.db.dao.MealRecordDao
import com.chopcal.app.data.db.dao.WeightEntryDao
import com.chopcal.app.data.db.entity.DailyMetricEntity
import com.chopcal.app.data.db.entity.MealRecordEntity
import com.chopcal.app.data.db.entity.WeightEntryEntity

@Database(
    entities = [MealRecordEntity::class, DailyMetricEntity::class, WeightEntryEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class ChopDatabase : RoomDatabase() {
    abstract fun mealRecordDao(): MealRecordDao
    abstract fun dailyMetricDao(): DailyMetricDao
    abstract fun weightEntryDao(): WeightEntryDao
}
