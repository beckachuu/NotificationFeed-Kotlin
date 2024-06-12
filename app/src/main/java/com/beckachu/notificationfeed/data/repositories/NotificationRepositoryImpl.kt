package com.beckachu.notificationfeed.data.repositories


import android.content.Context
import android.content.SharedPreferences
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import com.beckachu.notificationfeed.Const
import com.beckachu.notificationfeed.data.SharedPrefsManager
import com.beckachu.notificationfeed.data.entities.AppEntity
import com.beckachu.notificationfeed.data.entities.NotificationEntity
import com.beckachu.notificationfeed.data.local.dao.AppDao
import com.beckachu.notificationfeed.data.local.dao.NotificationDao
import com.beckachu.notificationfeed.data.remote.NotificationRemoteDataSource
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future


/*
    Common naming conventions for Repository methods:
    - getAll: Retrieves all records of a particular type from the data source.
    - getById: Retrieves a single record of a particular type by its unique identifier.
    - add (insert): Inserts a new record into the data source.
    - update: Updates an existing record in the data source.
    - delete (remove): Deletes an existing record from the data source.
    - find: Retrieves records that meet certain criteria.
*/


/**
 * Map the Entity (database) and the Model (UI)
 * (So basically the UI layer will use this class to get all the data it needs,
 * without directly interacting with the database)
 */
class NotificationRepositoryImpl(
    private val executor: ExecutorService,
    private val notifDao: NotificationDao,
    private val notifRemoteDataSource: NotificationRemoteDataSource,
    private val appDao: AppDao,
) {
    fun getAllNotifications(deleted: Boolean) = Pager(PagingConfig(pageSize = Const.PAGE_SIZE)) {
        if (deleted) notifDao.getDeletedNotifications() else notifDao.getNotificationPages()
    }.flow

    fun getAllNotificationsByApp(packageName: String) =
        Pager(PagingConfig(pageSize = Const.PAGE_SIZE)) {
            notifDao.getAllByApp(packageName)
        }.flow

    fun getAllNotificationsFiltered(
        query: String
    ): PagingSource<Int, NotificationEntity> {
        return notifDao.getFilteredNotifications("%$query%")
    }

    fun getAllNotificationsByAppFiltered(
        appPackage: String,
        query: String
    ): PagingSource<Int, NotificationEntity> {
        return notifDao.getFilteredNotificationsByApp("%$appPackage%", "%$query%")
    }

    fun getNotificationsByDateRange(
        startDate: Long,
        endDate: Long
    ): PagingSource<Int, NotificationEntity> {
        return notifDao.getNotificationsByDateRange(startDate, endDate)
    }

    fun getNotificationsByAppAndDateRange(
        appPackage: String,
        startDate: Long,
        endDate: Long
    ): PagingSource<Int, NotificationEntity> {
        return notifDao.getNotificationsByAppAndDateRange("%$appPackage%", startDate, endDate)
    }


    fun addNotif(
        context: Context, sharedPref: SharedPreferences,
        notificationEntity: NotificationEntity, userId: String?
    ) {
        executor.execute {
            synchronized(Const.LOCK_OBJECT) {
                if (!notificationEntity.isGroupSummary) {
                    val packageName = notificationEntity.packageName
                    val appEntity = AppEntity(context, packageName)
                    notifDao.insert(notificationEntity)

                    val checkedAppList =
                        SharedPrefsManager.getStringSet(sharedPref, SharedPrefsManager.APP_LIST)
                    if (!checkedAppList.contains(packageName)) {
                        appDao.insertApp(appEntity)
                        if (userId != null) {
                            notifRemoteDataSource.push(userId, notificationEntity)
                        }

                        val checkNewApp = SharedPrefsManager.getBool(
                            sharedPref,
                            SharedPrefsManager.CHECK_NEW_APP,
                            false
                        )
                        if (checkNewApp) {
                            checkedAppList.add(packageName)
                            sharedPref
                                .edit()
                                .putStringSet(SharedPrefsManager.APP_LIST, checkedAppList)
                                .apply()
                        }
                    }
                }
            }
        }
    }

    fun pullAndSave(context: Context, userId: String?): Boolean {
        return if (userId != null) {
            notifRemoteDataSource.pullAll(context, notifDao, appDao, userId)
            true
        } else {
            false
        }
    }

    fun pushAllToRemote(userId: String?): Boolean {
        return if (userId != null) {
            val notifications = notifDao.getAllNotifications()
            try {
                notifRemoteDataSource.pushAll(userId, notifications)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            true
        } else {
            false
        }
    }

    fun trashOrDelete(delete: Boolean, postTime: Long) {
        executor.execute {
            synchronized(Const.LOCK_OBJECT) {
                if (delete) {
                    notifDao.deleteOne(postTime)
                } else {
                    val currentTime = System.currentTimeMillis() / 1000
                    notifDao.moveToTrash(postTime, currentTime + Const.EXPIRE_TIME)
                }
            }
        }
    }

    fun scanTrash() {
        val currentTime = System.currentTimeMillis() / 1000
        executor.execute {
            synchronized(Const.LOCK_OBJECT) {
                notifDao.emptyTrash(currentTime)
            }
        }
    }


    fun deleteAllFromLocal(): Int {
        val future: Future<Int> = executor.submit(Callable {
            synchronized(Const.LOCK_OBJECT) {
                return@Callable notifDao.deleteAll()
            }
        })
        return try {
            future.get()
        } catch (e: InterruptedException) {
            0
        }
    }

    fun deleteAllFromRemote(userId: String?): Boolean {
        return if (userId != null) {
            notifRemoteDataSource.deleteAll(userId)
            true
        } else {
            false
        }
    }

}

