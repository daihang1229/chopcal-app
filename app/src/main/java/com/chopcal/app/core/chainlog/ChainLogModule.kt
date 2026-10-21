package com.chopcal.app.core.chainlog

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChainLogModule {

    @Binds
    @Singleton
    abstract fun bindChainedLogger(impl: FirebaseChainedLogger): ChainedLogger
}
