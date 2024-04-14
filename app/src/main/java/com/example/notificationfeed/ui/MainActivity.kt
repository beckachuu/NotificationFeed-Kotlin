package com.example.notificationfeed.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.notificationfeed.data.SharedPrefsManager
import com.example.notificationfeed.data.entities.AppEntity
import com.example.notificationfeed.domain.notif.NotificationListener
import com.example.notificationfeed.ui.nav_menu.NavigationPane
import com.example.notificationfeed.ui.theme.NotifFeedTheme
import com.example.notificationfeed.ui.viewmodels.AppListViewModel
import com.example.notificationfeed.ui.viewmodels.NotifListViewModel
import com.example.notificationfeed.ui.viewmodels.PermissionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val permissionViewModel: PermissionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotifFeedTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    AskPermission()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setContent {
            NotifFeedTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AskPermission()
                }
            }
        }
    }

    @Composable
    fun AskPermission() {
        if (!permissionViewModel.isNotificationServiceEnabled()) {
            PermissionDialog(onConfirm = permissionViewModel::openNotificationAccessSettings)
        } else {
            startService(Intent(this, NotificationListener::class.java))
            MainLayout()
        }
    }

    @Composable
    fun MainLayout() {
        val drawerState = rememberDrawerState(DrawerValue.Closed)

        val appListViewModel: AppListViewModel = hiltViewModel()
        val appList = appListViewModel.appList.observeAsState(emptyList<AppEntity>())

        val navController = rememberNavController()

        val notifListViewModel: NotifListViewModel = hiltViewModel()
        val notifListFlow = notifListViewModel.notifList.collectAsLazyPagingItems()

        val sharedPrefs: SharedPreferences = getSharedPreferences(
            SharedPrefsManager.DEFAULT_NAME,
            Context.MODE_PRIVATE
        )
        val isLoggedIn = remember { mutableStateOf(sharedPrefs.getBoolean("isLoggedIn", false)) }

        val onLoginClick = {
        }

        NavigationPane(
            drawerState,
            appList.value,
            navController,
            notifListFlow,
            isLoggedIn.value,
            onLoginClick
        )

    }
}
