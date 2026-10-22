package com.chopcal.app.data.repository

import com.chopcal.app.data.db.dao.WeightEntryDao
import com.chopcal.app.data.db.entity.WeightEntryEntity
import com.chopcal.app.domain.model.WeightEntry
import com.chopcal.app.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeightRepositoryImpl @Inject constructor(
    private val weightEntryDao: WeightEntryDao,
) : WeightRepository {

    override fun observeWeights(): Flow<List<WeightEntry>> =
        weightEntryDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun saveWeight(entry: WeightEntry) {
        weightEntryDao.upsert(WeightEntryEntity.fromDomain(entry))
    }

    override suspend fun deleteWeight(dateEpochDay: Long) {
        weightEntryDao.deleteByDate(dateEpochDay)
    }
}
