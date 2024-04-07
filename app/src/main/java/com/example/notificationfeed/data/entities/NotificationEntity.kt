package com.example.notificationfeed.data.entities

import android.app.Notification
import android.content.Context
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.NotificationListenerService.Ranking
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.notificationfeed.BuildConfig
import com.example.notificationfeed.Const
import com.example.notificationfeed.domain.notif.NotifListener
import com.example.notificationfeed.utils.Util
import org.json.JSONObject
import java.util.TimeZone

/**
 * This class represents the Entity stored in the database
 */
@RequiresApi(Build.VERSION_CODES.O)
@Entity
class NotificationEntity(@Ignore var context: Context, sbn: StatusBarNotification) {

    @Ignore
    var notif: Notification

    // Text
    private var appName: String? = null
    private var tickerText: String? = null
    var title: String = ""
    private var titleBig: String? = null
    var text: String = ""
    var textBig: String = ""

    @PrimaryKey(autoGenerate = true)
    val nid: Long = 0
    val packageName: String
    private var postTime: Long = 0
    private var systemTime: Long = 0
    private var isClearable = false
    private var isOngoing = false
    private var number = 0
    private var flags = 0
    private var defaults = 0
    private var ledARGB = 0
    private var ledOff = 0
    private var ledOn = 0

    // Device
    private var ringerMode = 0
    private var isScreenOn = false
    private var batteryLevel = 0
    private var batteryStatus: String? = null

    //    public boolean isConnected;
    //    public String connectionType;
    private var lastActivity: String? = null
    private var lastLocation: String? = null

    // Compat
    private var group: String? = null
    var isGroupSummary = false
    private var category: String? = null
    private var actionCount = 0
    private var isLocalOnly = false

    @Ignore
    private var style: String? = null

    // 16
    private var priority = 0

    // 18
    private var tag: String? = null

    // 20
    private var key: String? = null
    private var sortKey: String? = null

    // 21
    private var visibility = 0
    private var color = 0
    private var interruptionFilter = 0
    private var listenerHints = 0
    private var isMatchesInterruptionFilter = false

    private var textInfo: String? = null
    private var textSub: String? = null
    private var textSummary: String? = null
    private var textLines: String? = null

    init {
        notif = sbn.notification
        packageName = sbn.packageName
        postTime = sbn.postTime
        systemTime = System.currentTimeMillis()
        isClearable = sbn.isClearable
        isOngoing = sbn.isOngoing
        tag = sbn.tag
        key = sbn.key
        sortKey = notif.sortKey
        extract()
        if (Const.ENABLE_ACTIVITY_RECOGNITION || Const.ENABLE_LOCATION_SERVICE) {
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            lastActivity = sp.getString(Const.PREF_LAST_ACTIVITY, null)
            lastLocation = sp.getString(Const.PREF_LAST_LOCATION, null)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun extract() {
        // General
        flags = notif.flags
        number = -1

        // Device
        ringerMode = Util.getRingerMode(context)
        isScreenOn = Util.isScreenOn(context)
        batteryLevel = Util.getBatteryLevel(context)
        batteryStatus = Util.getBatteryStatus(context)
        // isConnected = Util.isNetworkAvailable(context);
        // connectionType = Util.getConnectivityType(context);


        // 21
        visibility = notif.visibility
        color = notif.color
        listenerHints = NotifListener.listenerHints
        interruptionFilter = NotifListener.interruptionFilter
        val ranking = Ranking()
        val rankingMap: NotificationListenerService.RankingMap? = NotifListener.ranking
        if (rankingMap != null && rankingMap.getRanking(key, ranking)) {
            isMatchesInterruptionFilter = ranking.matchesInterruptionFilter()
        }

        // Compat
        group = NotificationCompat.getGroup(notif)
        isGroupSummary = NotificationCompat.isGroupSummary(notif)
        category = NotificationCompat.getCategory(notif)
        actionCount = NotificationCompat.getActionCount(notif)
        isLocalOnly = NotificationCompat.getLocalOnly(notif)
        val extras = NotificationCompat.getExtras(notif)
        appName = Util.getAppNameFromPackage(context, packageName, false)
        tickerText = Util.nullToEmptyString(notif.tickerText)
        if (extras != null) {
            style = extras.getString(NotificationCompat.EXTRA_TEMPLATE)

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
            val lines = extras.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES)
            if (lines != null) {
                textLines = ""
                for (line in lines) {
                    textLines += line.toString() + "\n"
                }
                textLines = textLines!!.trim { it <= ' ' }
            }
        }
    }

    override fun toString(): String {
        return try {
            val json = JSONObject()

            // General
            json.put("nid", nid)
            json.put("tag", tag)
            json.put("appName", appName)
            json.put("packageName", packageName)
            json.put("postTime", postTime)
            json.put("systemTime", systemTime)
            json.put("offset", TimeZone.getDefault().getOffset(systemTime))
            json.put("version", BuildConfig.VERSION_CODE)
            json.put("sdk", Build.VERSION.SDK_INT)
            json.put("isOngoing", isOngoing)
            json.put("isClearable", isClearable)
            json.put("number", number)
            json.put("flags", flags)
            json.put("defaults", defaults)
            json.put("ledARGB", ledARGB)
            json.put("ledOn", ledOn)
            json.put("ledOff", ledOff)

            // Device
//            json.put("ringerMode", ringerMode)
//            json.put("isScreenOn", isScreenOn)
//            json.put("batteryLevel", batteryLevel)
//            json.put("batteryStatus", batteryStatus)
//            json.put("isConnected", isConnected);
//            json.put("connectionType", connectionType);

            // Compat
            json.put("group", group)
            json.put("isGroupSummary", isGroupSummary)
            json.put("category", category)
            json.put("actionCount", actionCount)
            json.put("isLocalOnly", isLocalOnly)
            json.put("style", style)
            //json.put("displayName",    displayName);

            // Text
            json.put("tickerText", tickerText)
            json.put("title", title)
            json.put("titleBig", titleBig)
            json.put("text", text)
            json.put("textBig", textBig)
            json.put("textInfo", textInfo)
            json.put("textSub", textSub)
            json.put("textSummary", textSummary)
            json.put("textLines", textLines)

            // 16
            json.put("priority", priority)

            // 20
            json.put("key", key)
            json.put("sortKey", sortKey)

            // 21
            json.put("visibility", visibility)
            json.put("color", color)
            json.put("interruptionFilter", interruptionFilter)
            json.put("listenerHints", listenerHints)
            json.put("matchesInterruptionFilter", isMatchesInterruptionFilter)


            // Activity
            //            if (Const.ENABLE_ACTIVITY_RECOGNITION && lastActivity != null) {
            //                json.put("lastActivity", new JSONObject(lastActivity));
            //            }

            // Location
            //            if (Const.ENABLE_LOCATION_SERVICE && lastLocation != null) {
            //                json.put("lastLocation", new JSONObject(lastLocation));
            //            }
            json.toString()
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
            ""
        }
    }
}
