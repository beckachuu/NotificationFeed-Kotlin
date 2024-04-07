package com.example.notificationfeed.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.notificationfeed.data.entities.NotificationEntity
import kotlinx.coroutines.flow.Flow

/**
 * This interface handles all operations with data in the database
 *
 *
 * Common naming conventions for DAO methods:
 * insert, get, update, and delete
 */
@Dao
interface NotificationDao {
    /**
     * Get all notifications by ASC id.
     */
    @Query("SELECT * FROM notificationentity ORDER BY nid ASC")
    fun getAllNotifications(): Flow<List<NotificationEntity?>>

    @Query("SELECT * FROM notificationentity WHERE nid < :id AND packageName LIKE :packageName ORDER BY nid DESC LIMIT :pageSize")
    fun getAllOlderThanId(id: Long, pageSize: Int, packageName: String?): List<NotificationEntity?>?

    @Query("SELECT * FROM notificationentity WHERE packageName LIKE :packageName ORDER BY nid DESC LIMIT :pageSize")
    fun getNewest(pageSize: Int, packageName: String?): List<NotificationEntity?>?

    @Query("SELECT * FROM notificationentity WHERE nid = :id")
    fun getById(id: Int): NotificationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notificationentity: NotificationEntity?): Long

    @Query("DELETE FROM notificationentity WHERE nid = :id")
    fun delete(id: Int): Int
}
