package com.beckachu.notificationfeed.data

import android.content.SharedPreferences


object SharedPrefsManager {
    const val DEFAULT_NAME = "General"
    const val UNREAD_COUNT = "UNREAD_COUNT"

    const val LAST_NOTI_KEY = "LAST_KEY"
    const val LAST_NOTI_TITLE = "LAST_NOTI_TITLE"
    const val LAST_NOTI_TEXT = "LAST_NOTI_TEXT"

    const val APP_LIST = "APP_LIST"
    const val RECORD_CHECKED = "RECORD_PREF"
    fun getBool(prefs: SharedPreferences, key: String?, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    fun putBool(prefs: SharedPreferences, key: String?, value: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getString(prefs: SharedPreferences, key: String?, defaultValue: String?): String? {
        return prefs.getString(key, defaultValue)
    }

    fun putString(prefs: SharedPreferences, key: String?, value: String?) {
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getInt(prefs: SharedPreferences, key: String?, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    fun putInt(prefs: SharedPreferences, key: String?, value: Int) {
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getStringSet(prefs: SharedPreferences, key: String?): HashSet<String> {
        val set = prefs.getStringSet(key, null)
        return if (set != null) {
            HashSet(set)
        } else HashSet()
    }

    fun putStringSet(prefs: SharedPreferences, key: String?, list: Set<String>) {
        val set: Set<String> = list
        val editor = prefs.edit()
        editor.putStringSet(key, set)
        editor.apply()
    }
}

