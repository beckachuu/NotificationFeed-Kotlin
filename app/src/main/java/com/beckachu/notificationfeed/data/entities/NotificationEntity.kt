package com.beckachu.notificationfeed.data.entities

import android.content.Context
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.NotificationListenerService.Ranking
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.beckachu.notificationfeed.domain.notif.NotificationListener
import com.beckachu.notificationfeed.utils.Util

/**
 * This class represents the Entity stored in the database
 */
@Entity
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val nid: Long = 0,

    val packageName: String,
    val postTime: Long = 0,
    var systemTime: Long = 0,
    var isClearable: Boolean = false,
    var isOngoing: Boolean = false,
    var number: Int = 0,
    var flags: Int = 0,

    // Device
    var ringerMode: Int = 0,
    var isScreenOn: Boolean = false,
    var batteryLevel: Int = 0,
    var batteryStatus: String? = null,

    // Compat
    var group: String? = null,
    var isGroupSummary: Boolean = false,
    var category: String? = null,
    var actionCount: Int = 0,
    var isLocalOnly: Boolean = false,

    // 16
    var priority: Int = 0,

    // 18
    var tag: String? = null,

    // 20
    var key: String? = null,
    var sortKey: String? = null,

    // 21
    var visibility: Int = 0,
    var color: Int = 0,
    var interruptionFilter: Int = 0,
    var listenerHints: Int = 0,
    var isMatchesInterruptionFilter: Boolean = false,

    var textInfo: String? = null,
    var textSub: String? = null,
    var textSummary: String? = null,
    var textLines: String? = null,

    // Text
    var appName: String? = null,
    var tickerText: String? = null,
    var title: String = "",
    var titleBig: String? = null,
    var text: String = "",
    var textBig: String = ""
) {
    @Ignore
    constructor(context: Context, sbn: StatusBarNotification) : this(
        packageName = sbn.packageName,
        postTime = sbn.postTime,
        systemTime = System.currentTimeMillis(),
        isClearable = sbn.isClearable,
        isOngoing = sbn.isOngoing,
        tag = sbn.tag,
        key = sbn.key,
        sortKey = sbn.notification.sortKey,
        flags = sbn.notification.flags,
        visibility = sbn.notification.visibility,
        color = sbn.notification.color,
        number = -1,

        // Compat
        group = NotificationCompat.getGroup(sbn.notification),
        isGroupSummary = NotificationCompat.isGroupSummary(sbn.notification),
        category = NotificationCompat.getCategory(sbn.notification),
        actionCount = NotificationCompat.getActionCount(sbn.notification),
        isLocalOnly = NotificationCompat.getLocalOnly(sbn.notification),
        tickerText = Util.nullToEmptyString(sbn.notification.tickerText)
    ) {
        // Device
        ringerMode = Util.getRingerMode(context)
        isScreenOn = Util.isScreenOn(context)
        batteryLevel = Util.getBatteryLevel(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            batteryStatus = Util.getBatteryStatus(context)
        }
        // isConnected = Util.isNetworkAvailable(context);
        // connectionType = Util.getConnectivityType(context);

        // 21
        listenerHints = NotificationListener.listenerHints
        interruptionFilter = NotificationListener.interruptionFilter
        val ranking = Ranking()
        val rankingMap: NotificationListenerService.RankingMap? = NotificationListener.ranking
        if (rankingMap != null && rankingMap.getRanking(key, ranking)) {
            isMatchesInterruptionFilter = ranking.matchesInterruptionFilter()
        }

        val extras = NotificationCompat.getExtras(sbn.notification)
        appName = Util.getAppNameFromPackage(context, packageName, false)
        if (extras != null) {
            // Text
            title = Util.nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_TITLE))
            titleBig =
                Util.nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_TITLE_BIG))
            text = Util.nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_TEXT))
            textBig =
                Util.nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_BIG_TEXT))
            textInfo =
                Util.nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_INFO_TEXT))
            textSub =
                Util.nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_SUB_TEXT))
            textSummary =
                Util.nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_SUMMARY_TEXT))
        }
    }
}
