package com.beckachu.notificationfeed.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.beckachu.notificationfeed.data.entities.NotificationEntity
import com.beckachu.notificationfeed.data.repositories.NotificationRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class NotifListViewModel @Inject constructor(
    notificationRepositoryImpl: NotificationRepositoryImpl
) : ViewModel() {

    val selectedPackageName = MutableStateFlow<String?>(null)
    val selectedAppName = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val notifList: Flow<PagingData<NotificationEntity>> =
        selectedPackageName.flatMapLatest { selectedApp ->
            if (selectedApp == null) {
                notificationRepositoryImpl.getAllNotifications(false)
            } else {
                notificationRepositoryImpl.getAllNotificationsByApp(selectedApp)
            }
        }

    val deletedList: Flow<PagingData<NotificationEntity>> =
        notificationRepositoryImpl.getAllNotifications(true)
}

