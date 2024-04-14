package com.beckachu.notificationfeed.navigation

sealed class Routes(val route: String) {
    data object Home : Routes("home")
    data object Analytics : Routes("analytics")
    data object Important : Routes("important")
    data object Settings : Routes("settings")
    data object Trash : Routes("trash")
}
