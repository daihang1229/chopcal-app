package com.chopcal.app.domain.repository

import com.chopcal.app.domain.model.WeightEntry
import kotlinx.coroutines.flow.Flow

/**
 * 称重记录仓储（Room `weight_entries`）。
 */
interface WeightRepository {

    /** 观察全部称重记录（按日期升序）。 */
    fun observeWeights(): Flow<List<WeightEntry>>

    suspend fun saveWeight(entry: WeightEntry)

    suspend fun deleteWeight(dateEpochDay: Long)
}
