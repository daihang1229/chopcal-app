package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.MealRecord
import com.chopcal.app.domain.model.MealType
import com.chopcal.app.domain.repository.RecordRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 保存一餐多条饮食记录（一餐多菜），支持「添加到该餐（累计）/ 替换该餐（仅保留本次）」。
 */
@Singleton
class SaveMealRecordsUseCase @Inject constructor(
    private val recordRepository: RecordRepository,
) {

    enum class Mode { ADD, REPLACE }

    /** 某餐已有的记录（用于弹窗判断）。 */
    suspend fun existing(dateEpochDay: Long, mealType: MealType): List<MealRecord> =
        recordRepository.mealsFor(dateEpochDay, mealType)

    /** 保存；[Mode.REPLACE] 时先删该餐全部再插入本次。 */
    suspend operator fun invoke(
        dateEpochDay: Long,
        mealType: MealType,
        records: List<MealRecord>,
        mode: Mode,
    ) {
        if (mode == Mode.REPLACE) {
            recordRepository.deleteMeals(dateEpochDay, mealType)
        }
        recordRepository.saveMeals(records)
    }
}
