package com.example.notificationfeed.ui.nav_menu

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.notificationfeed.data.entities.AppEntity
import com.example.notificationfeed.navigation.Routes

@Composable
fun DrawerContent(
    appList: List<AppEntity?>?,
    navController: NavHostController,
    isLoggedIn: Boolean,
) {

    LazyColumn {
        item {
            // User info section
            UserInfoPanel(isLoggedIn)
            Spacer(modifier = Modifier.height(16.dp))

            // Important, Settings, and Trash items
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

            Divider()
        }

        // App list section
        items(appList?.size ?: 0) { index ->
            val appName = appList?.get(index)?.appName
            val appIcon = appList?.get(index)?.iconByte?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                bitmap.asImageBitmap()
            }
            NavigationDrawerItem(
                label = { Text(text = appName ?: "") },
                icon = { appIcon?.let { Icon(bitmap = it, contentDescription = appName) } },
                selected = false,
                onClick = { navController.navigate(Routes.AppRoutes(appName ?: "").route) }
            )
        }
    }
}
