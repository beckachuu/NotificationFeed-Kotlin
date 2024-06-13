package com.beckachu.notificationfeed.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.beckachu.notificationfeed.data.entities.AppEntity
import com.beckachu.notificationfeed.data.entities.NotificationEntity
import com.beckachu.notificationfeed.data.local.dao.AppDao
import com.beckachu.notificationfeed.data.local.dao.NotificationDao


@Database(
    version = 4,
    entities = [NotificationEntity::class, AppEntity::class],
    exportSchema = true
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
                    .addMigrations(MIGRATION_1_3, MIGRATION_3_4)
                    .build()
            }
            return db as AppDatabase
        }

        private val MIGRATION_1_3 = object : Migration(1, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE new_NotificationEntity (
                notifKey TEXT NOT NULL,
                packageName TEXT NOT NULL,
                postTime INTEGER PRIMARY KEY NOT NULL,
                isClearable INTEGER NOT NULL,
                isOngoing INTEGER NOT NULL,
                flags INTEGER NOT NULL,
                ringerMode INTEGER NOT NULL,
                isScreenOn INTEGER NOT NULL,
                batteryLevel INTEGER NOT NULL,
                batteryStatus TEXT,
                "group" TEXT,
                isGroupSummary INTEGER NOT NULL,
                category TEXT,
                actionCount INTEGER NOT NULL,
                isLocalOnly INTEGER NOT NULL,
                priority INTEGER NOT NULL,
                tag TEXT,
                sortKey TEXT,
                visibility INTEGER NOT NULL,
                color INTEGER NOT NULL,
                interruptionFilter INTEGER NOT NULL,
                listenerHints INTEGER NOT NULL,
                isMatchesInterruptionFilter INTEGER NOT NULL,
                textInfo TEXT,
                textSub TEXT,
                textSummary TEXT,
                textLines TEXT,
                appName TEXT,
                tickerText TEXT,
                title TEXT NOT NULL,
                titleBig TEXT,
                text TEXT NOT NULL,
                textBig TEXT NOT NULL,
                expireTime INTEGER NULL  -- Add the new column from MIGRATION_2_3 here
            )
            """
                )
                db.execSQL(
                    """
            INSERT INTO new_NotificationEntity (notifKey, packageName, postTime, isClearable, isOngoing, flags, ringerMode, isScreenOn, batteryLevel, batteryStatus, "group", isGroupSummary, category, actionCount, isLocalOnly, priority, tag, sortKey, visibility, color, interruptionFilter, listenerHints, isMatchesInterruptionFilter, textInfo, textSub, textSummary, textLines, appName, tickerText, title, titleBig, text, textBig, expireTime)
            SELECT key, packageName, postTime, isClearable, isOngoing, flags, ringerMode, isScreenOn, batteryLevel, batteryStatus, "group", isGroupSummary, category, actionCount, isLocalOnly, priority, tag, sortKey, visibility, color, interruptionFilter, listenerHints, isMatchesInterruptionFilter, textInfo, textSub, textSummary, textLines, appName, tickerText, title, titleBig, text, textBig, NULL -- Set expireTime to NULL for existing records
            FROM NotificationEntity
            """
                )
                db.execSQL("DROP TABLE NotificationEntity")
                db.execSQL("ALTER TABLE new_NotificationEntity RENAME TO NotificationEntity")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE NotificationEntity ADD COLUMN favorite INTEGER NOT NULL DEFAULT 0")
            }
        }

    }
}

