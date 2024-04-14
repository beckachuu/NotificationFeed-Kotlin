package com.beckachu.notificationfeed.domain.notif

import android.content.Context
import android.content.SharedPreferences
import android.service.notification.StatusBarNotification
import com.beckachu.notificationfeed.Const
import com.beckachu.notificationfeed.data.SharedPrefsManager
import com.beckachu.notificationfeed.data.SharedPrefsManager.getBool
import com.beckachu.notificationfeed.data.SharedPrefsManager.getInt
import com.beckachu.notificationfeed.data.SharedPrefsManager.getString
import com.beckachu.notificationfeed.data.SharedPrefsManager.getStringSet
import com.beckachu.notificationfeed.data.SharedPrefsManager.putInt
import com.beckachu.notificationfeed.data.SharedPrefsManager.putString
import com.beckachu.notificationfeed.data.di.NotifRepoModule
import com.beckachu.notificationfeed.data.entities.NotificationEntity
import com.beckachu.notificationfeed.data.repositories.NotificationRepositoryImpl


class NotifHandler internal constructor(private val context: Context) {
    private val notificationRepositoryImpl: NotificationRepositoryImpl =
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
        notificationRepositoryImpl.addNotif(
            context,
            notifEntity
        )

        // Update new notification count
        val unreadCount = getInt(sharedPrefs, SharedPrefsManager.UNREAD_COUNT, 0)
        putInt(sharedPrefs, SharedPrefsManager.UNREAD_COUNT, unreadCount + 1)
        if (Const.DEBUG) println("Added notif [" + sbn.key + "]: " + notifEntity.text)
    }
}

