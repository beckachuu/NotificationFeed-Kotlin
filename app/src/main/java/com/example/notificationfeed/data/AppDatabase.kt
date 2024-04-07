package com.example.notificationfeed.data

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notificationfeed.data.entities.AppEntity
import com.example.notificationfeed.data.entities.NotificationEntity
import com.example.notificationfeed.data.local.dao.AppDao
import com.example.notificationfeed.data.local.dao.NotificationDao


@RequiresApi(Build.VERSION_CODES.O)
@Database(
    version = 1,
    entities = [NotificationEntity::class, AppEntity::class],
    autoMigrations = [],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notiDao(): NotificationDao
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
                // Create the new table
//            database.execSQL("DROP TABLE IF EXISTS myappentity");
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS myappentity " +
                            "(packageName TEXT PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "appName TEXT, isFavorite BOOLEAN, isReceivingNoti BOOLEAN)"
                )

                // Copy the data
//            database.execSQL("INSERT INTO users_new (userid, username, last_update) SELECT userid, username, last_update FROM users");

                // Remove the old table
//            database.execSQL("DROP TABLE users");

                // Change the table name to the correct one
                // database.execSQL("ALTER TABLE users_new RENAME TO users");
            }
        }
    }
}

