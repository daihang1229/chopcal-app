package com.chopcal.app.data.di

import com.chopcal.app.data.datastore.UserProfileRepositoryImpl
import com.chopcal.app.data.remote.FakeDishRecognizer
import com.chopcal.app.data.repository.DishRepositoryImpl
import com.chopcal.app.data.repository.RecordRepositoryImpl
import com.chopcal.app.domain.repository.DishRecognizer
import com.chopcal.app.domain.repository.DishRepository
import com.chopcal.app.domain.repository.RecordRepository
import com.chopcal.app.domain.repository.UserProfileRepository
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
    abstract fun bindDishRepository(impl: DishRepositoryImpl): DishRepository

    @Binds
    @Singleton
    abstract fun bindDishRecognizer(impl: FakeDishRecognizer): DishRecognizer
}
