package com.beckachu.notificationfeed.data.di

import com.beckachu.notificationfeed.data.remote.NotificationRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NotifRemoteDataSourceModule {
    @Provides
    fun provideNotificationRemoteDataSource(firebaseDb: FirebaseFirestore): NotificationRemoteDataSource {
        return NotificationRemoteDataSource(firebaseDb)
    }
}
