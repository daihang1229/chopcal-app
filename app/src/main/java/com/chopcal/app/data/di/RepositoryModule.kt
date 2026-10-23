package com.chopcal.app.data.di

import com.chopcal.app.data.datasource.local.AssetsJsonContentProvider
import com.chopcal.app.data.datasource.local.JsonContentProvider
import com.chopcal.app.data.datastore.UserProfileRepositoryImpl
import com.chopcal.app.data.remote.FakeDishRecognizer
import com.chopcal.app.data.repository.DishRepositoryImpl
import com.chopcal.app.data.repository.HealthTipRepositoryImpl
import com.chopcal.app.data.repository.IngredientRepositoryImpl
import com.chopcal.app.data.repository.RecordRepositoryImpl
import com.chopcal.app.data.repository.WeightRepositoryImpl
import com.chopcal.app.domain.repository.DishRecognizer
import com.chopcal.app.domain.repository.DishRepository
import com.chopcal.app.domain.repository.HealthTipRepository
import com.chopcal.app.domain.repository.IngredientRepository
import com.chopcal.app.domain.repository.RecordRepository
import com.chopcal.app.domain.repository.UserProfileRepository
import com.chopcal.app.domain.repository.WeightRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(impl: UserProfileRepositoryImpl): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindRecordRepository(impl: RecordRepositoryImpl): RecordRepository

    @Binds
    @Singleton
    abstract fun bindWeightRepository(impl: WeightRepositoryImpl): WeightRepository

    @Binds
    @Singleton
    abstract fun bindDishRepository(impl: DishRepositoryImpl): DishRepository

    @Binds
    @Singleton
    abstract fun bindDishRecognizer(impl: FakeDishRecognizer): DishRecognizer

    @Binds
    @Singleton
    abstract fun bindIngredientRepository(impl: IngredientRepositoryImpl): IngredientRepository

    @Binds
    @Singleton
    abstract fun bindHealthTipRepository(impl: HealthTipRepositoryImpl): HealthTipRepository

    @Binds
    @Singleton
    abstract fun bindJsonContentProvider(impl: AssetsJsonContentProvider): JsonContentProvider
}
