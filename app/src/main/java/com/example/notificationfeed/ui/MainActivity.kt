package com.example.notificationfeed.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, NotificationListener::class.java))
            } else {
                startService(Intent(this, NotificationListener::class.java))
            }
            MainLayout()
        }
    }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainLayout() {
    val appListViewModel: AppListViewModel = hiltViewModel()
    val appList = appListViewModel.appList.observeAsState(emptyList())

    val notifListViewModel: NotifListViewModel = hiltViewModel()
    val notifList by notifListViewModel.notifList.observeAsState(emptyList())

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    NavigationPane(drawerState, appList.value, navController)

    // Display the notification list
    NotificationModelList(notifList)
}
