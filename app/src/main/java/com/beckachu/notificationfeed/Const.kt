package com.beckachu.notificationfeed

import androidx.compose.ui.unit.dp


object Const {
    val DEBUG: Boolean = BuildConfig.DEBUG
    val LOCK_OBJECT = Any()
    val PAGE_SIZE = 40

    // Navigation menu stuff
    val TOP_PADDING = 24.dp
    val LEFT_PADDING = 21.dp
    val VERTICAL_PADDING = 16.dp
    val NAV_MENU_WIDTH = 0.7f
    val ROW_HEIGHT = 48.dp
    val BIG_ROW_HEIGHT = 52.dp

    // Collections
    val users = "users"
    val notifCollection = "notificationCollection"

    const val ALL_NOTI = "%"
    const val MAX_FRAGMENT_CACHE = 5

    // Feature flags
    const val ENABLE_ACTIVITY_RECOGNITION = true
    const val ENABLE_LOCATION_SERVICE = true

    // Preferences not shown in the UI
    const val PREF_LAST_ACTIVITY = "pref_last_activity"
    const val PREF_LAST_LOCATION = "pref_last_location"

    // Intent actions
    const val UPDATE_NEWEST = "com.beckachu.notificationfeed.UPDATE_NEWEST"
}