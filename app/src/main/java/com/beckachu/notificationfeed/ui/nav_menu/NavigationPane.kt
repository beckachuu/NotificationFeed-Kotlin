package com.beckachu.notificationfeed.ui.nav_menu

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.beckachu.notificationfeed.Const
import com.beckachu.notificationfeed.data.entities.AppEntity
import com.beckachu.notificationfeed.data.entities.NotificationEntity
import com.beckachu.notificationfeed.ui.appbar.AppBar
import com.beckachu.notificationfeed.ui.notification.NotificationModelList
import com.beckachu.notificationfeed.ui.sign_in.SignInState
import com.beckachu.notificationfeed.ui.sign_in.UserData
import com.beckachu.notificationfeed.ui.viewmodels.NotifListViewModel


@Composable
fun NavigationPane(
    drawerState: DrawerState,
    appList: List<AppEntity?>?,
    navController: NavHostController,
    notifListViewModel: NotifListViewModel,
    notifList: LazyPagingItems<NotificationEntity>,
    state: SignInState,
    onSignInClick: () -> Unit,
    userData: UserData?,
    context: Context
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val maxDrawerWidth = screenWidth * Const.NAV_MENU_WIDTH

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Surface(color = MaterialTheme.colorScheme.surface) {
                Box(
                    Modifier
                        .widthIn(max = maxDrawerWidth)
                        .fillMaxHeight()
                ) {
                    DrawerContent(
                        screenWidth,
                        appList,
                        navController,
                        state,
                        onSignInClick,
                        userData,
                        notifListViewModel
                    )
                }
            }
        },
        gesturesEnabled = true,
    ) {
        Scaffold(
            topBar = {
                AppBar(drawerState, notifListViewModel, context)
            },

            content = { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    NotificationModelList(notifList, context)
                }
            }
        )
    }
}

