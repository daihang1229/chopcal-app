package com.chopcal.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chopcal.app.data.db.entity.DailyMetricEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyMetricDao {

    @Query("SELECT * FROM daily_metrics WHERE dateEpochDay = :date")
    fun observeByDate(date: Long): Flow<DailyMetricEntity?>

    @Query("SELECT * FROM daily_metrics WHERE dateEpochDay = :date")
    suspend fun getByDate(date: Long): DailyMetricEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DailyMetricEntity)
}
