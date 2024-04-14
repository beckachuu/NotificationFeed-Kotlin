package com.example.notificationfeed.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.example.notificationfeed.data.entities.NotificationEntity
import com.example.notificationfeed.data.repositories.NotificationRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NotifListViewModel @Inject constructor(
    notificationRepositoryImpl: NotificationRepositoryImpl
) : ViewModel() {
    val notifList: Flow<PagingData<NotificationEntity>> =
        notificationRepositoryImpl.getAllNotifications()
}

