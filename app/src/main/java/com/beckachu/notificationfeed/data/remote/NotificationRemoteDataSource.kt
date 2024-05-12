package com.beckachu.notificationfeed.data.remote

import android.content.Context
import com.beckachu.notificationfeed.Const
import com.beckachu.notificationfeed.data.entities.AppEntity
import com.beckachu.notificationfeed.data.entities.NotificationEntity
import com.beckachu.notificationfeed.data.local.dao.AppDao
import com.beckachu.notificationfeed.data.local.dao.NotificationDao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationRemoteDataSource(firebaseDb: FirebaseFirestore) {
    private val db: FirebaseFirestore = firebaseDb

    fun push(userId: String, notificationEntity: NotificationEntity) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection(Const.users)
                    .document(userId)
                    .collection(Const.notifCollection)
                    .add(notificationEntity)
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    fun pullAll(context: Context, notifDao: NotificationDao, appDao: AppDao, userId: String) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val querySnapshot = db.collection(Const.users)
                    .document(userId)
                    .collection(Const.notifCollection).get().await()
                val notifications =
                    querySnapshot.documents.mapNotNull { it.toObject(NotificationEntity::class.java) }

                notifications.forEach { notification ->
                    if (!notification.isGroupSummary) {
                        val appEntity = AppEntity(context, notification.packageName)
                        notifDao.insert(notification)
                        appDao.insertApp(appEntity)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    fun pushAll() {

    }

    fun removeFromRemote(userId: String, notificationEntity: NotificationEntity) =
        CoroutineScope(Dispatchers.IO).launch {
            try {

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

}
