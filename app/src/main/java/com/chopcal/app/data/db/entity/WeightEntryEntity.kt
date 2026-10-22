package com.chopcal.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chopcal.app.domain.model.WeightEntry

/**
 * 称重记录（同一天只保留一条，重复称重覆盖）。
 */
@Entity(tableName = "weight_entries")
data class WeightEntryEntity(
    @PrimaryKey val dateEpochDay: Long,
    val weightKg: Double,
) {
    fun toDomain(): WeightEntry = WeightEntry(
        dateEpochDay = dateEpochDay,
        weightKg = weightKg,
    )

    companion object {
        fun fromDomain(entry: WeightEntry): WeightEntryEntity = WeightEntryEntity(
            dateEpochDay = entry.dateEpochDay,
            weightKg = entry.weightKg,
        )
    }
}
