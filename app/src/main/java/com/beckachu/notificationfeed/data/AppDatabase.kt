package com.beckachu.notificationfeed.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.beckachu.notificationfeed.data.entities.AppEntity
import com.beckachu.notificationfeed.data.entities.NotificationEntity
import com.beckachu.notificationfeed.data.local.dao.AppDao
import com.beckachu.notificationfeed.data.local.dao.NotificationDao


@Database(
    version = 1,
    entities = [NotificationEntity::class, AppEntity::class],
    autoMigrations = [],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notifDao(): NotificationDao
    abstract fun myAppDao(): AppDao

    companion object {
        private var db: AppDatabase? = null
        private const val DATABASE_NAME = "notifeed.db"

        /**
         * @param context: You should use app context (getApplicationContext()) to avoid
         * the activity or service be killed by the system (which leads to
         * unexpected issues).
         */
        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (db == null) {
                db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, DATABASE_NAME
                )
                    .build()
            }
            return db as AppDatabase
        }
    }
}

