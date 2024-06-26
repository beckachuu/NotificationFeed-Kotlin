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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.beckachu.notificationfeed.Const
import com.beckachu.notificationfeed.data.di.NotifRepoModule
import com.beckachu.notificationfeed.data.entities.AppEntity
import com.beckachu.notificationfeed.data.repositories.NotificationRepositoryImpl
import com.beckachu.notificationfeed.navigation.Routes
import com.beckachu.notificationfeed.ui.appbar.AppBar
import com.beckachu.notificationfeed.ui.notification.NotificationModelList
import com.beckachu.notificationfeed.ui.screens.AnalyticsScreen
import com.beckachu.notificationfeed.ui.screens.SettingsScreen
import com.beckachu.notificationfeed.ui.sign_in.SignInState
import com.beckachu.notificationfeed.ui.sign_in.UserData
import com.beckachu.notificationfeed.ui.viewmodels.NotifListViewModel


@Composable
fun NavigationPane(
    drawerState: DrawerState,
    appList: List<AppEntity?>?,
    navController: NavHostController,
    notifListViewModel: NotifListViewModel,
    state: SignInState,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit,
    userData: UserData?,
    context: Context
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val maxDrawerWidth = screenWidth * Const.NAV_MENU_WIDTH
    val notifList = notifListViewModel.notifList.collectAsLazyPagingItems()
    val deletedList = notifListViewModel.deletedList.collectAsLazyPagingItems()
    val favoriteList = notifListViewModel.favoriteNotifications.collectAsLazyPagingItems()
    val notificationRepositoryImpl: NotificationRepositoryImpl =
        NotifRepoModule.provideNotifRepository(context)

    notificationRepositoryImpl.scanTrash();

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
                        onSignOutClick,
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
                    NavHost(navController, startDestination = Routes.Home.route) {
                        composable(Routes.Home.route) {
                            NotificationModelList(
                                notifList,
                                screenWidth,
                                notificationRepositoryImpl,
                                context,
                            )
                        }
                        composable(Routes.Analytics.route) {
                            AnalyticsScreen(
                                notifList.itemSnapshotList.items
                            )
                        }
                        composable(Routes.Favorites.route) {
                            NotificationModelList(
                                favoriteList,
                                screenWidth,
                                notificationRepositoryImpl,
                                context,
                            )
                        }
                        composable(Routes.Settings.route) { SettingsScreen(appList) }
                        composable(Routes.Trash.route) {
                            NotificationModelList(
                                deletedList,
                                screenWidth,
                                notificationRepositoryImpl,
                                context,
                            )
                        }
                    }
                }
            }
        )

    }
}

