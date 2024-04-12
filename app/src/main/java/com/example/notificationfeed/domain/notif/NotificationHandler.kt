package com.example.notificationfeed.domain.notif

import android.content.Context
import android.content.SharedPreferences
import android.service.notification.StatusBarNotification
import com.example.notificationfeed.Const
import com.example.notificationfeed.data.SharedPrefsManager
import com.example.notificationfeed.data.SharedPrefsManager.getBool
import com.example.notificationfeed.data.SharedPrefsManager.getInt
import com.example.notificationfeed.data.SharedPrefsManager.getString
import com.example.notificationfeed.data.SharedPrefsManager.getStringSet
import com.example.notificationfeed.data.SharedPrefsManager.putInt
import com.example.notificationfeed.data.SharedPrefsManager.putString
import com.example.notificationfeed.data.di.NotifRepoModule
import com.example.notificationfeed.data.entities.NotificationEntity
import com.example.notificationfeed.data.repositories.NotificationRepository


class NotifHandler internal constructor(private val context: Context) {
    private val notificationRepository: NotificationRepository =
        NotifRepoModule.provideAppRepository(context)
    private val sharedPrefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        SharedPrefsManager.DEFAULT_NAME,
        Context.MODE_PRIVATE
    )

    fun handlePosted(sbn: StatusBarNotification) {
        // Don't save duplicated notifications
        val notifEntity = NotificationEntity(context, sbn)
        val lastKey = getString(sharedPrefs, SharedPrefsManager.LAST_NOTI_KEY, "")
        val currentKey = sbn.key
        val lastTitle = getString(sharedPrefs, SharedPrefsManager.LAST_NOTI_TITLE, "")
        val currentTitle: String = notifEntity.title
        val lastText = getString(sharedPrefs, SharedPrefsManager.LAST_NOTI_TEXT, "")
        val currentText: String = notifEntity.text
        if (lastKey == currentKey && lastTitle == currentTitle && lastText == currentText) {
            if (Const.DEBUG) println("DUPLICATED [" + lastKey + "]: " + notifEntity.text)
            return
        }
        // Don't save non-selected apps
        val recordAppList = getStringSet(sharedPrefs, SharedPrefsManager.APP_LIST)
        val recordChecked = getBool(sharedPrefs, SharedPrefsManager.RECORD_CHECKED, false)
        val packageName: String = notifEntity.packageName
        if ((recordAppList.contains(packageName) && !recordChecked) ||
            (!recordAppList.contains(packageName) && recordChecked)
        ) {
            if (Const.DEBUG) println("User chose not to record from this app")
            return
        }

        // Now it's good to add new notification
        putString(sharedPrefs, SharedPrefsManager.LAST_NOTI_KEY, currentKey)
        putString(sharedPrefs, SharedPrefsManager.LAST_NOTI_TITLE, currentTitle)
        putString(sharedPrefs, SharedPrefsManager.LAST_NOTI_TEXT, currentText)
        notificationRepository.addNotif(
            context,
            notifEntity
        )

        // Update new notification count
        val unreadCount = getInt(sharedPrefs, SharedPrefsManager.UNREAD_COUNT, 0)
        putInt(sharedPrefs, SharedPrefsManager.UNREAD_COUNT, unreadCount + 1)
        if (Const.DEBUG) println("Added notif [" + sbn.key + "]: " + notifEntity.text)
    }
}

