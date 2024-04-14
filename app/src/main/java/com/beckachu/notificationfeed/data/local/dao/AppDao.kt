package com.beckachu.notificationfeed.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beckachu.notificationfeed.data.entities.AppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT COUNT(packageName) FROM appentity")
    fun countAll(): Int

    @get:Query("SELECT * FROM appentity ORDER BY appName ASC")
    val allByNameAsc: Flow<List<AppEntity?>>

    @get:Query("SELECT DISTINCT packageName FROM notificationentity ORDER BY appName ASC")
    val packageNamesFromNotif: List<String?>?

    // Insert an app
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertApp(appentity: AppEntity)

    // Set app's favorite preference
    @Query("UPDATE appentity SET isFavorite = :pref WHERE packageName = :packageName")
    fun setFavorite(packageName: String?, pref: Boolean): Int

    // Set app's notification preference
    @Query("UPDATE appentity SET isReceivingNotif = :pref WHERE packageName = :packageName")
    fun setReceiveNoti(packageName: String?, pref: Boolean): Int
}
