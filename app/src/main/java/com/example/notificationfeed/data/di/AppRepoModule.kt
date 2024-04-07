package com.example.notificationfeed.data.di

import android.content.Context
import com.example.notificationfeed.data.AppDatabase
import com.example.notificationfeed.data.repositories.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppRepoModule {
    @Singleton
    @Provides
    fun provideAppRepository(@ApplicationContext context: Context): AppRepository {
        val executor = ExecutorModule.provideExecutor()

        val db: AppDatabase = AppDatabase.getInstance(context)
        val appDao = db.myAppDao()
        return AppRepository(context, executor, appDao)
    }
}
