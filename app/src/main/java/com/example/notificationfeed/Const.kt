package com.example.notificationfeed


object Const {
    val DEBUG: Boolean = BuildConfig.DEBUG
    val LOCK_OBJECT = Any()
    val PAGE_SIZE = 20
    const val NEGATIVE = -1
    const val ALL_NOTI = "%"
    const val MAX_FRAGMENT_CACHE = 5

    // Feature flags
    const val ENABLE_ACTIVITY_RECOGNITION = true
    const val ENABLE_LOCATION_SERVICE = true

    // Preferences not shown in the UI
    const val PREF_LAST_ACTIVITY = "pref_last_activity"
    const val PREF_LAST_LOCATION = "pref_last_location"

    // Intent actions
    const val UPDATE_NEWEST = "com.example.notificationfeed.UPDATE_NEWEST"
}