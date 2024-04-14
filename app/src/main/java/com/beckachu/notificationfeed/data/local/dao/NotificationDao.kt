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
    @Query("SELECT * FROM notificationentity ORDER BY nid DESC")
    fun getAllNotifications(): PagingSource<Int, NotificationEntity>

    @Query("SELECT * FROM notificationentity WHERE packageName LIKE :packageName ORDER BY nid DESC")
    fun getAllByApp(packageName: String?): PagingSource<Int, NotificationEntity>

    @Query("SELECT * FROM notificationentity WHERE nid = :id")
    fun getById(id: Int): NotificationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notificationentity: NotificationEntity): Long

    @Query("DELETE FROM notificationentity WHERE nid = :id")
    fun delete(id: Int): Int
}
