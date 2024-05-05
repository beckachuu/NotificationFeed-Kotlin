package com.beckachu.notificationfeed.data.di

import android.content.Context
import com.beckachu.notificationfeed.data.AppDatabase
import com.beckachu.notificationfeed.data.local.dao.NotificationDao
import com.beckachu.notificationfeed.data.repositories.NotificationRepositoryImpl
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
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
    fun provideNotifRepository(@ApplicationContext context: Context): NotificationRepositoryImpl {
        val executor = ExecutorModule.provideExecutor()

        val db: AppDatabase = AppDatabase.getInstance(context)
        val notifDao: NotificationDao = db.notifDao()
        val appDao = db.myAppDao()
        val firebaseDb = Firebase.firestore
        val notifRemoteDataSource =
            NotifRemoteDataSourceModule.provideNotificationRemoteDataSource(firebaseDb)

        return NotificationRepositoryImpl(
            executor,
            notifDao,
            notifRemoteDataSource,
            appDao
        )
    }
}
