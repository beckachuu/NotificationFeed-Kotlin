package com.beckachu.notificationfeed.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.os.BatteryManager
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.PermissionChecker
import com.beckachu.notificationfeed.BuildConfig
import com.beckachu.notificationfeed.Const
import java.text.DateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale


object Util {
    var format = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())

    val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)

    fun getAppNameFromPackage(
        context: Context,
        packageName: String?,
        returnNull: Boolean
    ): String? {
        val pm = context.applicationContext.packageManager
        val ai: ApplicationInfo? = try {
            pm.getApplicationInfo(packageName!!, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return if (returnNull) {
            if (ai == null) null else pm.getApplicationLabel(ai).toString()
        } else (if (ai != null) pm.getApplicationLabel(ai) else packageName) as String?
    }

    fun getAppIconFromPackage(context: Context, packageName: String?): Drawable? {
        val pm = context.applicationContext.packageManager
        var drawable: Drawable? = null
        try {
            val ai = pm.getApplicationInfo(packageName!!, 0)
            drawable = pm.getApplicationIcon(ai)
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
        return drawable
    }

    @Composable
    fun Drawable.toImageBitmap(): ImageBitmap {
        if (this is BitmapDrawable) {
            return bitmap.asImageBitmap()
        }

        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)

        return bitmap.asImageBitmap()
    }


    fun getAppIconFromByteArray(context: Context, byteArray: ByteArray): Drawable {
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return BitmapDrawable(context.resources, bitmap)
    }

    fun nullToEmptyString(charSequence: CharSequence?): String {
        return charSequence?.toString() ?: ""
    }

    fun isNotifAccessEnabled(context: Context): Boolean {
        try {
            val contentResolver = context.contentResolver
            val listeners =
                Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
            return !(listeners == null || !listeners.contains(BuildConfig.APPLICATION_ID + "/"))
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
        return false
    }

    fun getRingerMode(context: Context): Int {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            return am.ringerMode
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
        return -1
    }

    fun isScreenOn(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        try {
            return pm.isInteractive
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
        return false
    }

    fun getLocale(context: Context): String {
        val localeList = context.resources.configuration.locales
        return localeList.toString()
    }

    fun hasPermission(context: Context?, permission: String?): Boolean {
        return PermissionChecker.checkSelfPermission(
            context!!,
            permission!!
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    fun getBatteryLevel(context: Context): Int {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        try {
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
        return -1
    }


    fun getBatteryStatus(context: Context): String {
        try {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            return when (val status = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "charging"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "discharging"
                BatteryManager.BATTERY_STATUS_FULL -> "full"
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "not charging"
                BatteryManager.BATTERY_STATUS_UNKNOWN -> "unknown"
                else -> "" + status
            }
        } catch (e: Exception) {
            if (Const.DEBUG) e.printStackTrace()
        }
        return "undefined"
    }

    fun openApp(context: Context, packageName: String) {
        val manager = context.packageManager
        try {
            val intent = manager.getLaunchIntentForPackage(packageName)
                ?: throw PackageManager.NameNotFoundException()
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            context.startActivity(intent)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }


    //    public static boolean isNetworkAvailable(Context context) {
    //        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    //        if (cm != null) {
    //            try {
    //                NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
    //                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    //            } catch (Exception e) {
    //                if (Const.DEBUG) e.printStackTrace();
    //            }
    //        }
    //        return false;
    //    }
    //    public static String getConnectivityType(Context context) {
    //        try {
    //            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    //            if (cm != null) {
    //                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    //                if (networkInfo != null) {
    //                    int type = networkInfo.getType();
    //                    switch (type) {
    //                        case ConnectivityManager.TYPE_BLUETOOTH:
    //                            return "bluetooth";
    //                        case ConnectivityManager.TYPE_DUMMY:
    //                            return "dummy";
    //                        case ConnectivityManager.TYPE_ETHERNET:
    //                            return "ethernet";
    //                        case ConnectivityManager.TYPE_MOBILE:
    //                            return "mobile";
    //                        case ConnectivityManager.TYPE_MOBILE_DUN:
    //                            return "mobile dun";
    //                        case ConnectivityManager.TYPE_VPN:
    //                            return "vpn";
    //                        case ConnectivityManager.TYPE_WIFI:
    //                            return "wifi";
    //                        case ConnectivityManager.TYPE_WIMAX:
    //                            return "wimax";
    //                        default:
    //                            return "" + type;
    //                    }
    //                } else {
    //                    return "none";
    //                }
    //            }
    //        } catch (Exception e) {
    //            if (Const.DEBUG) e.printStackTrace();
    //        }
    //        return "undefined";
    //    }
}