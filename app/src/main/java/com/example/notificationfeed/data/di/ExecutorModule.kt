package com.example.notificationfeed.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExecutorModule {
    @Singleton
    @Provides
    fun provideExecutor(): ExecutorService = Executors.newSingleThreadExecutor()
}
