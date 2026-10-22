package com.chopcal.app.data.db

import android.content.Context
import androidx.room.Room
import com.chopcal.app.data.db.dao.DailyMetricDao
import com.chopcal.app.data.db.dao.MealRecordDao
import com.chopcal.app.data.db.dao.WeightEntryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideChopDatabase(@ApplicationContext context: Context): ChopDatabase =
        Room.databaseBuilder(context, ChopDatabase::class.java, "chopcal.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideMealRecordDao(db: ChopDatabase): MealRecordDao = db.mealRecordDao()

    @Provides
    fun provideDailyMetricDao(db: ChopDatabase): DailyMetricDao = db.dailyMetricDao()

    @Provides
    fun provideWeightEntryDao(db: ChopDatabase): WeightEntryDao = db.weightEntryDao()
}
