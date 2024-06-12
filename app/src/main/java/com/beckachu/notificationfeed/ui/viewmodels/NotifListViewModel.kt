package com.beckachu.notificationfeed.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.beckachu.notificationfeed.data.entities.NotificationEntity
import com.beckachu.notificationfeed.data.repositories.NotificationRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class NotifListViewModel @Inject constructor(
    notificationRepositoryImpl: NotificationRepositoryImpl
) : ViewModel() {

    val selectedPackageName = MutableStateFlow<String?>(null)
    val selectedAppName = MutableStateFlow<String?>(null)

    val isSearchMode = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")

    fun toggleSearchMode() {
        isSearchMode.value = !isSearchMode.value
        if (!isSearchMode.value) {
            searchQuery.value = ""
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val notifList: Flow<PagingData<NotificationEntity>> =
        combine(selectedPackageName, searchQuery) { selectedApp, query ->
            Pager(PagingConfig(pageSize = 20)) {
                if (selectedApp == null) {
                    notificationRepositoryImpl.getAllNotificationsFiltered(query)
                } else {
                    notificationRepositoryImpl.getAllNotificationsByAppFiltered(selectedApp, query)
                }
            }.flow
        }.flatMapLatest { it }


    val deletedList: Flow<PagingData<NotificationEntity>> =
        notificationRepositoryImpl.getAllNotifications(true)
}

