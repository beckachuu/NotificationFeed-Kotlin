package com.example.notificationfeed.ui.navigation

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.notificationfeed.data.entities.AppEntity
import kotlinx.coroutines.launch


@Composable
fun NavigationPane(
    drawerState: DrawerState,
    appList: List<AppEntity?>?,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // User info section
            UserInfoPanel()

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

            // App list section
            appList?.forEach { appEntity ->
                val appName = appEntity?.appName
                val appIcon = appEntity?.iconByte?.let {
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
        },
        gesturesEnabled = true,
    ) {
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("Show drawer") },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                    onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }
                )
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                NavHost(navController, startDestination = Routes.Home.route) {
                    composable(Routes.Home.route) { /* Home screen */ }
                    composable(Routes.Important.route) { /* Important screen */ }
                    composable(Routes.Settings.route) { /* Settings screen */ }
                    composable(Routes.Trash.route) { /* Trash screen */ }
                    composable("app/{appName}") { backStackEntry ->
                        val appName = backStackEntry.arguments?.getString("appName")

                        /* App screen */
                        
                    }
                }
            }
        }
    }
}

