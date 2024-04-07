package com.example.notificationfeed.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.notificationfeed.data.entities.AppEntity
import com.example.notificationfeed.data.repositories.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppListViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {
    val appList: LiveData<List<AppEntity?>> = appRepository.allAppByNameAsc.asLiveData()
}

