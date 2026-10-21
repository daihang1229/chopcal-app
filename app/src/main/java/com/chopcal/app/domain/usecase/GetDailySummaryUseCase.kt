package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.DailySummary
import com.chopcal.app.domain.model.MealSummary
import com.chopcal.app.domain.repository.RecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 按天汇总摄入（热量 + 宏量 + 各餐 + 水/步）。
 */
@Singleton
class GetDailySummaryUseCase @Inject constructor(
    private val recordRepository: RecordRepository,
) {

    operator fun invoke(dateEpochDay: Long): Flow<DailySummary> =
        combine(
            recordRepository.observeMeals(dateEpochDay),
            recordRepository.observeMetric(dateEpochDay),
        ) { meals, metric ->
            DailySummary(
                dateEpochDay = dateEpochDay,
                totalKcal = meals.sumOf { it.kcal },
                proteinG = meals.sumOf { it.proteinG },
                carbsG = meals.sumOf { it.carbsG },
                fatG = meals.sumOf { it.fatG },
                perMeal = meals.groupBy { it.mealType }.mapValues { (_, list) ->
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
