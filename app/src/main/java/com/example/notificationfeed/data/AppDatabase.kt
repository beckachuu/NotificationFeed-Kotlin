package com.example.notificationfeed.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notificationfeed.data.entities.AppEntity
import com.example.notificationfeed.data.entities.NotificationEntity
import com.example.notificationfeed.data.local.dao.AppDao
import com.example.notificationfeed.data.local.dao.NotificationDao


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
                ) //                .addMigrations(MIGRATION_1_2)
                    .build()
            }
            return db as AppDatabase
        }

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create a new table with the same name but without the removed field
                db.execSQL("CREATE TABLE new_NotificationEntity (/* new schema */)")

                // Copy the data from the old table to the new table
                db.execSQL("INSERT INTO new_NotificationEntity SELECT /* columns except the removed one */ FROM NotificationEntity")

                // Remove the old table
                db.execSQL("DROP TABLE NotificationEntity")

                // Rename the new table to the old table's name
                db.execSQL("ALTER TABLE new_NotificationEntity RENAME TO NotificationEntity")
            }
        }

    }
}

