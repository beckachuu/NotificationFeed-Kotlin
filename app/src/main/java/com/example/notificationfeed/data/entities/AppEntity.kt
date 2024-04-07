package com.example.notificationfeed.data.entities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notificationfeed.utils.Util
import java.io.ByteArrayOutputStream

@Entity
class AppEntity(context: Context, @PrimaryKey var packageName: String) {
    var appName: String? = null

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var iconByte: ByteArray? = null

    private var isFavorite: Boolean // User get an "unswipable" notification if this app has new notification
    private var isReceivingNotif: Boolean // Record only, no "unswipable" stuff

    init {
        appName = Util.getAppNameFromPackage(context, packageName, false)
        val icon = Util.getAppIconFromPackage(context, packageName)
        val bitmap = (icon as BitmapDrawable?)!!.bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        iconByte = stream.toByteArray()
        isFavorite = false
        isReceivingNotif = true
    }
}
