package com.example.notificationfeed.ui.nav_menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import com.example.notificationfeed.data.entities.AppEntity
import com.example.notificationfeed.data.entities.NotificationEntity
import com.example.notificationfeed.navigation.NavGraph
import com.example.notificationfeed.ui.notification.NotificationModelList


@Composable
fun NavigationPane(
    drawerState: DrawerState,
    appList: List<AppEntity?>?,
    navController: NavHostController,
    notifList: LazyPagingItems<NotificationEntity>,
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(appList, navController, isLoggedIn)
        },
        gesturesEnabled = true,
    ) {
        Scaffold(
            topBar = {
                AppBar(drawerState, onLoginClick)
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

