package com.example.notificationfeed.data.repositories


import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.notificationfeed.Const
import com.example.notificationfeed.data.entities.AppEntity
import com.example.notificationfeed.data.entities.NotificationEntity
import com.example.notificationfeed.data.local.dao.AppDao
import com.example.notificationfeed.data.local.dao.NotificationDao
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
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
    private val appDao: AppDao,
) {
    fun getAllNotifications() = Pager(PagingConfig(pageSize = Const.PAGE_SIZE)) {
        notifDao.getAllNotifications()
    }.flow

    fun addNotif(context: Context, notificationEntity: NotificationEntity) {
        executor.execute {
            synchronized(Const.LOCK_OBJECT) {
                if (!notificationEntity.isGroupSummary) {
                    notifDao.insert(notificationEntity)
                    appDao.insertApp(AppEntity(context, notificationEntity.packageName))
                }
            }
        }
    }

    fun deleteNoti(id: String): Int {
        val future: Future<Int> = executor.submit(Callable<Int> {
            synchronized(Const.LOCK_OBJECT) {
                val intId = id.toInt()
                return@Callable notifDao.delete(intId)
            }
        })
        return try {
            future.get()
        } catch (e: InterruptedException) {
            0
        } catch (e: ExecutionException) {
            0
        }
    }

    fun deleteNoti(id: Int): Int {
        val future: Future<Int> = executor.submit(Callable<Int> {
            synchronized(Const.LOCK_OBJECT) {
                return@Callable notifDao.delete(id)
            }
        })
        return try {
            future.get()
        } catch (e: InterruptedException) {
            0
        } catch (e: ExecutionException) {
            0
        }
    }

    /**
     * For synchronizing remote and local data
     *
     *
     * TODO: when remote is implemented
     */
    fun synchronize() {
//        val userData = networkDataSource.fetchUserData()
//        localDataSource.saveUserData(userData)
    }
}

