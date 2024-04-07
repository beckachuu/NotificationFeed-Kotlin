package com.example.notificationfeed.data.models

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.notificationfeed.Const
import com.example.notificationfeed.data.entities.NotificationEntity
import com.example.notificationfeed.utils.Util
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat


/**
 * This class represents the UI model
 * (which is kinda similar to the Entity, except
 * this doesn't need all the Entity's data fields)
 */
@RequiresApi(Build.VERSION_CODES.O)
class NotificationModel(
    context: Context,
    notificationEntity: NotificationEntity,
    iconCache: HashMap<String?, Drawable?>,
    format: DateFormat,
    lastDate: String
) {
    val id: Long
    var packageName: String? = null
    var appName: String? = null
    var title: String = ""
    var text: String = ""
    var preview: String = ""
    var date: String = ""
    private var showDate = false

    init {
        id = notificationEntity.nid
        try {
            val json = JSONObject(notificationEntity.toString())
            packageName = json.getString("packageName")
            appName = Util.getAppNameFromPackage(context, packageName, false)
            title = json.optString("title")
            text = json.optString("text")
            preview = notificationEntity.textBig
            if (!iconCache.containsKey(packageName)) {
                iconCache[packageName] = Util.getAppIconFromPackage(context, packageName)
            }
            date = format.format(json.optLong("systemTime"))
            showDate = lastDate != date
        } catch (e: JSONException) {
            if (Const.DEBUG) e.printStackTrace()
        }
    }

    fun shouldShowDate(): Boolean {
        return showDate
    }

    fun setShowDate(showDate: Boolean) {
        this.showDate = showDate
    }
}

