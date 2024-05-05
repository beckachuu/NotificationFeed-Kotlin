package com.beckachu.notificationfeed.data.repositories


import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.beckachu.notificationfeed.Const
import com.beckachu.notificationfeed.data.entities.AppEntity
import com.beckachu.notificationfeed.data.entities.NotificationEntity
import com.beckachu.notificationfeed.data.local.dao.AppDao
import com.beckachu.notificationfeed.data.local.dao.NotificationDao
import com.beckachu.notificationfeed.data.remote.NotificationRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
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
    private val notifRemoteDataSource: NotificationRemoteDataSource,
    private val appDao: AppDao,
) {
    fun getAllNotifications() = Pager(PagingConfig(pageSize = Const.PAGE_SIZE)) {
        notifDao.getAllNotifications()
    }.flow

    fun getAllNotificationsByApp(packageName: String) =
        Pager(PagingConfig(pageSize = Const.PAGE_SIZE)) {
            notifDao.getAllByApp(packageName)
        }.flow

    fun addNotif(context: Context, notificationEntity: NotificationEntity, userId: String?) {
        executor.execute {
            synchronized(Const.LOCK_OBJECT) {
                if (!notificationEntity.isGroupSummary) {
                    val appEntity = AppEntity(context, notificationEntity.packageName)
                    notifDao.insert(notificationEntity)
                    appDao.insertApp(appEntity)

                    if (userId != null) {
                        notifRemoteDataSource.pushToRemote(userId, notificationEntity)
                    }
                }
            }
        }
    }

    fun deleteNotif(id: Int): Int {
        val future: Future<Int> = executor.submit(Callable {
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


    fun pullFromRemote(userId: String, db: FirebaseFirestore) {
        db.collection("users")
            .document(userId)
            .collection("NotificationEntity")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Convert each document to a NotificationEntity and update the local database
                    val notificationEntity = document.toObject(NotificationEntity::class.java)
                    executor.execute {
                        notifDao.insert(notificationEntity)
                    }
                }
            }
    }

}

