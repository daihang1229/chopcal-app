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
    val kcalMin: Int,
    val kcalMax: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    val ingredientsJson: String?,
    val thumbnailUri: String?,
    val createdAt: Long,
) {
    fun toDomain(): MealRecord = MealRecord(
        id = id,
        dateEpochDay = dateEpochDay,
        mealType = runCatching { MealType.valueOf(mealType) }.getOrDefault(MealType.SNACK),
        dishName = dishName,
        kcal = kcal,
        kcalMin = kcalMin,
        kcalMax = kcalMax,
        proteinG = proteinG,
        carbsG = carbsG,
        fatG = fatG,
        ingredientsJson = ingredientsJson,
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
            kcalMin = record.kcalMin,
            kcalMax = record.kcalMax,
            proteinG = record.proteinG,
            carbsG = record.carbsG,
            fatG = record.fatG,
            ingredientsJson = record.ingredientsJson,
            thumbnailUri = record.thumbnailUri,
            createdAt = record.createdAt,
        )
    }
}
