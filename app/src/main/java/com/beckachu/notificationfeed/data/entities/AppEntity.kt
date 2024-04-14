package com.beckachu.notificationfeed.data.entities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.beckachu.notificationfeed.utils.Util
import java.io.ByteArrayOutputStream

@Entity
data class AppEntity(
    @PrimaryKey
    var packageName: String,

    var appName: String? = null,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var iconByte: ByteArray? = null,

    var isFavorite: Boolean = false, // User get an "unswipable" notification if this app has new notification

    var isReceivingNotif: Boolean = true // Record only, no "unswipable" stuff
) {
    @Ignore
    constructor(context: Context, packageName: String) : this(
        packageName = packageName,
        appName = Util.getAppNameFromPackage(context, packageName, false)
    ) {
        val icon = Util.getAppIconFromPackage(context, packageName)
        val bitmap = (icon as BitmapDrawable?)!!.bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        iconByte = stream.toByteArray()
    }
}
