package com.example.notificationfeed.domain.notif


import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresApi
import com.example.notificationfeed.Const


class NotifListener : NotificationListenerService() {
    private var notificationHandler: NotifHandler? = null
    override fun onCreate() {
        super.onCreate()
        notificationHandler = NotifHandler(this)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
    }

    override fun onListenerDisconnected() {
        instance = null
        super.onListenerDisconnected()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        try {
            notificationHandler!!.handlePosted(sbn)
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
    }

//    override fun onNotificationRemoved(sbn: StatusBarNotification) {
//        try {
//            notificationHandler!!.handleRemoved(sbn)
//        } catch (e: Exception) {
//            if (Const.DEBUG) e.printStackTrace()
//        }
//    }
//
//    override fun onNotificationRemoved(sbn: StatusBarNotification, rankingMap: RankingMap) {
//        try {
//            notificationHandler!!.handleRemoved(sbn)
//        } catch (e: Exception) {
//            if (Const.DEBUG) e.printStackTrace()
//        }
//    }

    companion object {
        private var instance: NotifListener? = null
        val interruptionFilter: Int
            get() {
                if (instance != null) {
                    try {
                        return instance!!.currentInterruptionFilter
                    } catch (e: Exception) {
                        if (Const.DEBUG) e.printStackTrace()
                    }
                }
                return -1
            }
        val listenerHints: Int
            get() {
                if (instance != null) {
                    try {
                        return instance!!.currentListenerHints
                    } catch (e: Exception) {
                        if (Const.DEBUG) e.printStackTrace()
                    }
                }
                return -1
            }
        val ranking: RankingMap?
            get() {
                if (instance != null) {
                    try {
                        return instance!!.currentRanking
                    } catch (e: Exception) {
                        if (Const.DEBUG) e.printStackTrace()
                    }
                }
                return null
            }
    }
}

