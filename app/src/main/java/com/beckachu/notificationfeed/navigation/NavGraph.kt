package com.beckachu.notificationfeed.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.beckachu.notificationfeed.ui.screens.AnalyticsScreen
import com.beckachu.notificationfeed.ui.screens.ImportantScreen
import com.beckachu.notificationfeed.ui.screens.SettingsScreen
import com.beckachu.notificationfeed.ui.screens.TrashScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(navController, startDestination = Routes.Home.route) {
        composable(Routes.Home.route) { /* Home screen */ }

        composable(Routes.Analytics.route) { AnalyticsScreen() }
        composable(Routes.Important.route) { ImportantScreen() }
        composable(Routes.Settings.route) { SettingsScreen() }
        composable(Routes.Trash.route) { TrashScreen() }
    }
}

