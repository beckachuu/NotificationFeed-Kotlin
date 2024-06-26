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


    @Query("SELECT * FROM notificationentity WHERE (title LIKE :query OR textBig LIKE :query OR text LIKE :query) AND expireTime IS NULL ORDER BY postTime DESC")
    fun getFilteredNotifications(query: String): PagingSource<Int, NotificationEntity>

    @Query("SELECT * FROM notificationentity WHERE packageName LIKE :appPackage AND (title LIKE :query OR textBig LIKE :query OR text LIKE :query) AND expireTime IS NULL ORDER BY postTime DESC")
    fun getFilteredNotificationsByApp(
        appPackage: String,
        query: String
    ): PagingSource<Int, NotificationEntity>

    @Query("SELECT * FROM notificationentity WHERE postTime >= :startDate AND postTime <= :endDate AND expireTime IS NULL ORDER BY postTime DESC")
    fun getNotificationsByDateRange(
        startDate: Long,
        endDate: Long
    ): PagingSource<Int, NotificationEntity>

    @Query("SELECT * FROM notificationentity WHERE packageName LIKE :appPackage AND postTime >= :startDate AND postTime <= :endDate AND expireTime IS NULL ORDER BY postTime DESC")
    fun getNotificationsByAppAndDateRange(
        appPackage: String,
        startDate: Long,
        endDate: Long
    ): PagingSource<Int, NotificationEntity>


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

    @Query("UPDATE NotificationEntity SET favorite = :isFavorite WHERE postTime = :postTime")
    fun setFavorite(postTime: Long, isFavorite: Boolean)

    @Query("SELECT * FROM notificationentity WHERE favorite = 1 AND expireTime IS NULL ORDER BY postTime DESC")
    fun getFavorites(): PagingSource<Int, NotificationEntity>

}
