package com.beckachu.notificationfeed.ui.nav_menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.beckachu.notificationfeed.Const
import com.beckachu.notificationfeed.data.entities.AppEntity
import com.beckachu.notificationfeed.navigation.Routes
import com.beckachu.notificationfeed.ui.components.ShortDivider
import com.beckachu.notificationfeed.ui.sign_in.SignInState
import com.beckachu.notificationfeed.ui.sign_in.UserData
import com.beckachu.notificationfeed.ui.viewmodels.NotifListViewModel
import com.beckachu.notificationfeed.utils.Util
import com.beckachu.notificationfeed.utils.Util.toImageBitmap

@Composable
fun DrawerContent(
    screenWidth: Dp,
    appList: List<AppEntity?>?,
    navController: NavHostController,
    state: SignInState,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit,
    userData: UserData?,
    notifListViewModel: NotifListViewModel,
) {

    LazyColumn {
        item {
            // User info section
            UserInfoPanel(state, onSignInClick, onSignOutClick, userData, Modifier.fillMaxWidth())

            ShortDivider()

            NavigationDrawerItem(
                label = { Text(text = "Home") },
                icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                selected = false,
                onClick = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }

                    notifListViewModel.selectedPackageName.value = null
                    notifListViewModel.selectedAppName.value = null
                }
            )
            NavigationDrawerItem(
                label = { Text(text = "Analytics") },
                icon = { Icon(Icons.Filled.Create, contentDescription = "Analytics") },
                selected = false,
                onClick = {
                    navController.navigate(Routes.Analytics.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                    notifListViewModel.selectedAppName.value = "Analytics"
                }
            )
            NavigationDrawerItem(
                label = { Text(text = "Important") },
                icon = { Icon(Icons.Filled.Star, contentDescription = "Important") },
                selected = false,
                onClick = {
                    navController.navigate(Routes.Important.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                    notifListViewModel.selectedAppName.value = "Important"
                }
            )
            NavigationDrawerItem(
                label = { Text(text = "Settings") },
                icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                selected = false,
                onClick = {
                    navController.navigate(Routes.Settings.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                    notifListViewModel.selectedAppName.value = "Settings"
                }
            )
            NavigationDrawerItem(
                label = { Text(text = "Trash") },
                icon = { Icon(Icons.Filled.Delete, contentDescription = "Trash") },
                selected = false,
                onClick = {
                    navController.navigate(Routes.Trash.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                    notifListViewModel.selectedAppName.value = "Trash"
                }
            )

            ShortDivider()

            Text(
                text = "Apps",
                modifier = Modifier.padding(
                    start = Const.LEFT_PADDING,
                    top = Const.VERTICAL_PADDING,
                    bottom = 12.dp
                ),
                style = MaterialTheme.typography.labelMedium
            )
        }

        // App list section
        items(appList?.size ?: 0) { index ->
            val appName = appList?.get(index)?.appName
            val packageName = appList?.get(index)?.packageName
            val appIcon = Util.getAppIconFromPackage(LocalContext.current, packageName)

            NavigationDrawerItem(
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (appIcon != null) {
                            appIcon.toImageBitmap()?.let {
                                Image(
                                    bitmap = it,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = appName ?: "",
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },

                selected = false,
                onClick = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }

                    notifListViewModel.selectedPackageName.value = packageName
                    notifListViewModel.selectedAppName.value = appName
                }
            )
        }
    }
}
