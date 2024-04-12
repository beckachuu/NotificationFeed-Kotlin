package com.example.notificationfeed.navigation

sealed class Routes(val route: String) {
    data object Home : Routes("home")
    data object Important : Routes("important")
    data object Settings : Routes("settings")
    data object Trash : Routes("trash")
    data class AppRoutes(val appName: String) : Routes("app/$appName")
}
