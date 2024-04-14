package com.beckachu.notificationfeed.data.di

import android.content.Context
import com.beckachu.notificationfeed.data.AppDatabase
import com.beckachu.notificationfeed.data.repositories.AppRepositoryImpl
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
    fun provideAppRepository(@ApplicationContext context: Context): AppRepositoryImpl {
        val executor = ExecutorModule.provideExecutor()

        val db: AppDatabase = AppDatabase.getInstance(context)
        val appDao = db.myAppDao()
        return AppRepositoryImpl(executor, appDao)
    }
}
