package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.MealRecord
import com.chopcal.app.domain.model.MealType
import com.chopcal.app.domain.repository.RecordRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 保存一条饮食记录（识别结果落库）。
 */
@Singleton
class SaveMealRecordUseCase @Inject constructor(
    private val recordRepository: RecordRepository,
) {

    suspend operator fun invoke(
        dateEpochDay: Long,
        mealType: MealType,
        dishName: String,
        kcal: Int,
        proteinG: Int,
        carbsG: Int,
        fatG: Int,
        thumbnailUri: String? = null,
    ) {
        recordRepository.saveMeal(
            MealRecord(
                dateEpochDay = dateEpochDay,
                mealType = mealType,
                dishName = dishName,
                kcal = kcal,
                proteinG = proteinG,
                carbsG = carbsG,
                fatG = fatG,
                thumbnailUri = thumbnailUri,
            )
        )
    }
}
