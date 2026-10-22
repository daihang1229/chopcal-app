package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.DailyMetric
import com.chopcal.app.domain.model.DailySummary
import com.chopcal.app.domain.model.MealSummary
import com.chopcal.app.domain.repository.RecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 按日期区间 [startEpochDay, endEpochDay]（含端点）汇总每日摄入 + 水/步。
 *
 * 返回长度固定为区间天数的列表（无记录的天补 0），供卡路里图 / 步数饮水图 / 周小结使用。
 */
@Singleton
class GetDailySummariesUseCase @Inject constructor(
    private val recordRepository: RecordRepository,
) {

    operator fun invoke(startEpochDay: Long, endEpochDay: Long): Flow<List<DailySummary>> =
        combine(
            recordRepository.observeMealsRange(startEpochDay, endEpochDay),
            recordRepository.observeMetricsRange(startEpochDay, endEpochDay),
        ) { meals, metrics ->
            val mealsByDay = meals.groupBy { it.dateEpochDay }
            val metricByDay = metrics.associateBy { it.dateEpochDay }
            (startEpochDay..endEpochDay).map { day ->
                val dayMeals = mealsByDay[day].orEmpty()
                val metric = metricByDay[day] ?: DailyMetric(dateEpochDay = day)
                DailySummary(
                    dateEpochDay = day,
                    totalKcal = dayMeals.sumOf { it.kcal },
                    proteinG = dayMeals.sumOf { it.proteinG },
                    carbsG = dayMeals.sumOf { it.carbsG },
                    fatG = dayMeals.sumOf { it.fatG },
                    perMeal = dayMeals.groupBy { it.mealType }.mapValues { (_, list) ->
                        MealSummary(
                            kcal = list.sumOf { it.kcal },
                            thumbnailUri = list.firstNotNullOfOrNull { it.thumbnailUri },
                        )
                    },
                    waterMl = metric.waterMl,
                    stepCount = metric.stepCount,
                )
            }
        }
}
