package com.chopcal.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chopcal.app.domain.model.DailyMetric

@Entity(tableName = "daily_metrics")
data class DailyMetricEntity(
    @PrimaryKey val dateEpochDay: Long,
    val waterMl: Int = 0,
    val stepCount: Int = 0,
) {
    fun toDomain(): DailyMetric = DailyMetric(
        dateEpochDay = dateEpochDay,
        waterMl = waterMl,
        stepCount = stepCount,
    )

    companion object {
        fun fromDomain(metric: DailyMetric): DailyMetricEntity = DailyMetricEntity(
            dateEpochDay = metric.dateEpochDay,
            waterMl = metric.waterMl,
            stepCount = metric.stepCount,
        )
    }
}
