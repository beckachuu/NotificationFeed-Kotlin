package com.example.notificationfeed.ui.nav_menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import com.example.notificationfeed.Const
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
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val maxDrawerWidth = screenWidth * Const.NAV_MENU_WIDTH

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Surface(color = MaterialTheme.colorScheme.surface) {
                Box(Modifier.widthIn(max = maxDrawerWidth)) {
                    DrawerContent(screenWidth, appList, navController, isLoggedIn)
                }
            }
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

