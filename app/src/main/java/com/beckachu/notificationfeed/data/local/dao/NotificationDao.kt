package com.beckachu.notificationfeed.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beckachu.notificationfeed.data.entities.NotificationEntity

/**
 * This interface handles all operations with data in the database
 *
 *
 * Common naming conventions for DAO methods:
 * insert, get, update, and delete
 */
@Dao
interface NotificationDao {
    @Query("SELECT * FROM notificationentity WHERE expireTime IS NULL ORDER BY postTime DESC")
    fun getNotificationPages(): PagingSource<Int, NotificationEntity>

    @Query("SELECT * FROM notificationentity WHERE expireTime IS NULL")
    fun getAllNotifications(): List<NotificationEntity>

    @Query("SELECT * FROM notificationentity WHERE packageName LIKE :packageName AND expireTime IS NULL ORDER BY postTime DESC")
    fun getAllByApp(packageName: String?): PagingSource<Int, NotificationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notificationentity: NotificationEntity): Long


    @Query("SELECT * FROM notificationentity WHERE expireTime IS NOT NULL ORDER BY postTime DESC")
    fun getDeletedNotifications(): PagingSource<Int, NotificationEntity>

    @Query("UPDATE NotificationEntity SET expireTime = :expTime WHERE postTime = :key")
    fun moveToTrash(key: Long, expTime: Long)

    @Query("DELETE FROM NotificationEntity WHERE expireTime < :currentTime")
    fun emptyTrash(currentTime: Long)

    @Query("DELETE FROM NotificationEntity WHERE postTime = :key")
    fun deleteOne(key: Long)

    @Query("DELETE FROM notificationentity")
    fun deleteAll(): Int
}
