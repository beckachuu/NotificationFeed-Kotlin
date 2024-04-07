package com.example.notificationfeed.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.notificationfeed.ui.navigation.NavigationPane
import com.example.notificationfeed.ui.notification.NotificationModelList
import com.example.notificationfeed.ui.theme.NotifFeedTheme
import com.example.notificationfeed.ui.viewmodels.AppListViewModel
import com.example.notificationfeed.ui.viewmodels.NotifListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotifFeedTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainLayout()
                }
            }
        }
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
