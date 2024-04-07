package com.example.notificationfeed.data.repositories


import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.notificationfeed.Const
import com.example.notificationfeed.data.entities.AppEntity
import com.example.notificationfeed.data.entities.NotificationEntity
import com.example.notificationfeed.data.local.dao.AppDao
import com.example.notificationfeed.data.local.dao.NotificationDao
import kotlinx.coroutines.flow.Flow
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
class NotificationRepository(
    private val executor: ExecutorService,
    private val notifDao: NotificationDao,
    private val appDao: AppDao,
) {
    val allNotifByIdAsc: Flow<List<NotificationEntity?>> = notifDao.getAllNotifications()

    fun getAllNotifOlderThanId(id: Long, packageName: String?): List<NotificationEntity?>? {
        val future: Future<List<NotificationEntity?>?> = executor.submit(
            Callable<List<NotificationEntity?>?> {
                if (id != Const.NEGATIVE.toLong()) return@Callable notifDao.getAllOlderThanId(
                    id,
                    Const.PAGE_SIZE,
                    packageName
                ) else return@Callable notifDao.getNewest(
                    Const.PAGE_SIZE,
                    packageName
                )
            })
        return try {
            future.get()
        } catch (e: InterruptedException) {
            if (Const.DEBUG) e.printStackTrace()
            null
        } catch (e: ExecutionException) {
            if (Const.DEBUG) e.printStackTrace()
            null
        }
    }

    fun getNotiById(id: String): NotificationEntity? {
        val intId = id.toInt()
        val future: Future<NotificationEntity?> = executor.submit(
            Callable<NotificationEntity?> {
                notifDao.getById(intId)
            })
        return try {
            future.get()
        } catch (e: InterruptedException) {
            null
        } catch (e: ExecutionException) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNoti(context: Context, notificationEntity: NotificationEntity) {
        executor.execute {
            synchronized(Const.LOCK_OBJECT) {
                if (!notificationEntity.isGroupSummary) {
                    notifDao.insert(notificationEntity)
                    appDao.insertApp(AppEntity(context, notificationEntity.packageName))

                    // Update notification list screen

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

