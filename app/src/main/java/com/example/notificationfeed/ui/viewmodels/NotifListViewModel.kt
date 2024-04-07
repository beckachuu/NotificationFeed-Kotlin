package com.example.notificationfeed.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.notificationfeed.data.entities.NotificationEntity
import com.example.notificationfeed.data.repositories.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotifListViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    val notifList: LiveData<List<NotificationEntity?>> =
        notificationRepository.allNotifByIdAsc.asLiveData()
}
