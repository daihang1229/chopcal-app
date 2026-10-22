package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.WeightEntry
import com.chopcal.app.domain.repository.WeightRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 保存一条称重记录（`＋ 记录体重`，PROGRESS.md §3）。
 */
@Singleton
class RecordWeightUseCase @Inject constructor(
    private val weightRepository: WeightRepository,
) {

    suspend operator fun invoke(dateEpochDay: Long, weightKg: Double) {
        weightRepository.saveWeight(WeightEntry(dateEpochDay = dateEpochDay, weightKg = weightKg))
    }
}
