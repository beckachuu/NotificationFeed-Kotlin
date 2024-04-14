package com.beckachu.notificationfeed.domain.notif


import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.beckachu.notificationfeed.Const


class NotificationListener : NotificationListenerService() {
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
        private var instance: NotificationListener? = null
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

