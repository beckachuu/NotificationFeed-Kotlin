package com.example.notificationfeed.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(navController: NavHostController) {
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
