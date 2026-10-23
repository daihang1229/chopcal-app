package com.chopcal.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.chopcal.app.data.db.entity.MealRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealRecordDao {

    @Query("SELECT * FROM meal_records WHERE dateEpochDay = :date ORDER BY createdAt ASC")
    fun observeByDate(date: Long): Flow<List<MealRecordEntity>>

    @Query("SELECT * FROM meal_records WHERE dateEpochDay BETWEEN :start AND :end ORDER BY dateEpochDay ASC, createdAt ASC")
    fun observeRange(start: Long, end: Long): Flow<List<MealRecordEntity>>

    @Query("SELECT * FROM meal_records WHERE dateEpochDay = :date AND mealType = :mealType ORDER BY createdAt ASC")
    suspend fun byDateAndType(date: Long, mealType: String): List<MealRecordEntity>

    @Query("DELETE FROM meal_records WHERE dateEpochDay = :date AND mealType = :mealType")
    suspend fun deleteByDateAndType(date: Long, mealType: String)

    @Insert
    suspend fun insert(entity: MealRecordEntity): Long

    @Insert
    suspend fun insertAll(entities: List<MealRecordEntity>)

    @Delete
    suspend fun delete(entity: MealRecordEntity)

    @Query("SELECT * FROM meal_records WHERE id = :id")
    suspend fun byId(id: Long): MealRecordEntity?
}
