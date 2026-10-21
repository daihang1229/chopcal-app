package com.chopcal.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.chopcal.app.domain.model.MealRecord
import com.chopcal.app.domain.model.MealType

@Entity(
    tableName = "meal_records",
    indices = [Index(value = ["dateEpochDay"])],
)
data class MealRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val mealType: String,
    val dishName: String,
    val kcal: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    val thumbnailUri: String?,
    val createdAt: Long,
) {
    fun toDomain(): MealRecord = MealRecord(
        id = id,
        dateEpochDay = dateEpochDay,
        mealType = runCatching { MealType.valueOf(mealType) }.getOrDefault(MealType.SNACK),
        dishName = dishName,
        kcal = kcal,
        proteinG = proteinG,
        carbsG = carbsG,
        fatG = fatG,
        thumbnailUri = thumbnailUri,
        createdAt = createdAt,
    )

    companion object {
        fun fromDomain(record: MealRecord): MealRecordEntity = MealRecordEntity(
            id = record.id,
            dateEpochDay = record.dateEpochDay,
            mealType = record.mealType.name,
            dishName = record.dishName,
            kcal = record.kcal,
            proteinG = record.proteinG,
            carbsG = record.carbsG,
            fatG = record.fatG,
            thumbnailUri = record.thumbnailUri,
            createdAt = record.createdAt,
        )
    }
}
