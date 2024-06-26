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
    private val notificationRepositoryImpl: NotificationRepositoryImpl
) : ViewModel() {

    val selectedPackageName = MutableStateFlow<String?>(null)
    val selectedAppName = MutableStateFlow<String?>(null)

    val isSearchMode = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")

    val selectedDateRange = MutableStateFlow<Pair<Long, Long>?>(null)

    fun toggleSearchMode() {
        isSearchMode.value = !isSearchMode.value
        if (!isSearchMode.value) {
            searchQuery.value = ""
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val notifList: Flow<PagingData<NotificationEntity>> =
        combine(
            selectedPackageName,
            searchQuery,
            selectedDateRange
        ) { selectedApp, query, dateRange ->
            Pager(PagingConfig(pageSize = 20)) {
                when {
                    selectedApp != null && dateRange != null -> notificationRepositoryImpl.getNotificationsByAppAndDateRange(
                        selectedApp,
                        dateRange.first,
                        dateRange.second
                    )

                    selectedApp != null && dateRange == null -> notificationRepositoryImpl.getAllNotificationsByAppFiltered(
                        selectedApp,
                        query
                    )

                    selectedApp == null && dateRange != null -> notificationRepositoryImpl.getNotificationsByDateRange(
                        dateRange.first,
                        dateRange.second
                    )

                    else -> notificationRepositoryImpl.getAllNotificationsFiltered(
                        query
                    )
                }
            }.flow
        }.flatMapLatest { it }

    val favoriteNotifications: Flow<PagingData<NotificationEntity>> =
        notificationRepositoryImpl.getFavoriteNotifications()

    val deletedList: Flow<PagingData<NotificationEntity>> =
        notificationRepositoryImpl.getAllNotifications(true)
}

