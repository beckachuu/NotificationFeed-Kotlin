package com.example.notificationfeed.data.di

import android.content.Context
import com.example.notificationfeed.data.AppDatabase
import com.example.notificationfeed.data.local.dao.NotificationDao
import com.example.notificationfeed.data.repositories.NotificationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotifRepoModule {
    @Singleton
    @Provides
    fun provideAppRepository(@ApplicationContext context: Context): NotificationRepositoryImpl {
        val executor = ExecutorModule.provideExecutor()

        val db: AppDatabase = AppDatabase.getInstance(context)
        val notifDao: NotificationDao = db.notifDao()
        val appDao = db.myAppDao()
        return NotificationRepositoryImpl(executor, notifDao, appDao)
    }
}
