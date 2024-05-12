package com.beckachu.notificationfeed.data.repositories


import android.content.Context
import android.content.SharedPreferences
import androidx.paging.Pager
import androidx.paging.PagingConfig
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
    fun getAllNotifications() = Pager(PagingConfig(pageSize = Const.PAGE_SIZE)) {
        notifDao.getNotificationPages()
    }.flow

    fun getAllNotificationsByApp(packageName: String) =
        Pager(PagingConfig(pageSize = Const.PAGE_SIZE)) {
            notifDao.getAllByApp(packageName)
        }.flow

    fun addNotif(
        context: Context, sharedPref: SharedPreferences,
        notificationEntity: NotificationEntity, userId: String?
    ) {
        executor.execute {
            synchronized(Const.LOCK_OBJECT) {
                if (!notificationEntity.isGroupSummary) {
                    val appEntity = AppEntity(context, notificationEntity.packageName)
                    notifDao.insert(notificationEntity)

                    appDao.insertApp(appEntity)
                    val packageName = notificationEntity.packageName
                    val checkNewApp = SharedPrefsManager.getBool(
                        sharedPref,
                        SharedPrefsManager.CHECK_NEW_APP,
                        false
                    )
                    val checkedAppList =
                        SharedPrefsManager.getStringSet(sharedPref, SharedPrefsManager.APP_LIST)
                    if (checkNewApp && !checkedAppList.contains(packageName)) {
                        checkedAppList.add(packageName)
                        sharedPref
                            .edit()
                            .putStringSet(SharedPrefsManager.APP_LIST, checkedAppList)
                            .apply()
                    }

                    if (userId != null) {
                        notifRemoteDataSource.push(userId, notificationEntity)
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
            notifRemoteDataSource.pushAll(userId, notifications)
            true
        } else {
            false
        }
    }

    fun delete(key: String): Int {
        val future: Future<Int> = executor.submit(Callable {
            synchronized(Const.LOCK_OBJECT) {
                return@Callable notifDao.delete(key)
            }
        })
        return try {
            future.get()
        } catch (e: InterruptedException) {
            0
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

