package com.chopcal.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chopcal.app.data.db.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightEntryDao {

    @Query("SELECT * FROM weight_entries ORDER BY dateEpochDay ASC")
    fun observeAll(): Flow<List<WeightEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WeightEntryEntity)

    @Query("DELETE FROM weight_entries WHERE dateEpochDay = :date")
    suspend fun deleteByDate(date: Long)
}
