package com.beckachu.notificationfeed.ui.nav_menu

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
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
import kotlin.math.roundToInt

@Composable
fun DrawerContent(
    screenWidth: Dp,
    appList: List<AppEntity?>?,
    navController: NavHostController,
    state: SignInState,
    onSignInClick: () -> Unit,
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
                    notifListViewModel.selectedPackageName.value = null
                    notifListViewModel.selectedAppName.value = null
                }
            )
            NavigationDrawerItem(
                label = { Text(text = "Analytics") },
                icon = { Icon(Icons.Filled.Create, contentDescription = "Analytics") },
                selected = false,
                onClick = { navController.navigate(Routes.Analytics.route) }
            )
            NavigationDrawerItem(
                label = { Text(text = "Important") },
                icon = { Icon(Icons.Filled.Star, contentDescription = "Important") },
                selected = false,
                onClick = { navController.navigate(Routes.Important.route) }
            )
            NavigationDrawerItem(
                label = { Text(text = "Settings") },
                icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                selected = false,
                onClick = { navController.navigate(Routes.Settings.route) }
            )
            NavigationDrawerItem(
                label = { Text(text = "Trash") },
                icon = { Icon(Icons.Filled.Delete, contentDescription = "Trash") },
                selected = false,
                onClick = { navController.navigate(Routes.Trash.route) }
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
            val appIcon = appList?.get(index)?.iconByte?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                bitmap.asImageBitmap()
            }
            val packageName = appList?.get(index)?.packageName

            NavigationDrawerItem(
                label = {
                    Text(
                        text = appName ?: "",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                icon = {
                    appIcon?.let {
                        Icon(
                            bitmap = it,
                            contentDescription = appName,
                            modifier = Modifier.size(
                                minOf(
                                    screenWidth.value.roundToInt(),
                                    LocalConfiguration.current.screenHeightDp
                                ).dp * 0.1f
                            )
                        )
                    }
                },
                selected = false,
                onClick = {
                    notifListViewModel.selectedPackageName.value = packageName
                    notifListViewModel.selectedAppName.value = appName
                }
            )
        }
    }
}
