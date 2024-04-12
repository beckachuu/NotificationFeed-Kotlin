package com.example.notificationfeed.ui.nav_menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import com.example.notificationfeed.data.entities.AppEntity
import com.example.notificationfeed.data.entities.NotificationEntity
import com.example.notificationfeed.navigation.NavGraph
import com.example.notificationfeed.ui.notification.NotificationModelList
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationPane(
    drawerState: DrawerState,
    appList: List<AppEntity?>?,
    navController: NavHostController,
    notifList: LazyPagingItems<NotificationEntity>
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(appList, navController)
        },
        gesturesEnabled = true,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("App Title") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },

            content = { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    NavGraph(navController)
                    NotificationModelList(notifList)
                }
            }
        )
    }
}
