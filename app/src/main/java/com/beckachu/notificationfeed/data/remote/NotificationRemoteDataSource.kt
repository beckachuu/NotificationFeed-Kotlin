package com.beckachu.notificationfeed.data.remote

import android.content.Context
import android.util.Log
import com.beckachu.notificationfeed.Const
import com.beckachu.notificationfeed.data.entities.AppEntity
import com.beckachu.notificationfeed.data.entities.NotificationEntity
import com.beckachu.notificationfeed.data.local.dao.AppDao
import com.beckachu.notificationfeed.data.local.dao.NotificationDao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationRemoteDataSource(firebaseDb: FirebaseFirestore) {
    private val db: FirebaseFirestore = firebaseDb

    fun push(userId: String, notification: NotificationEntity) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection(Const.users)
                    .document(userId)
                    .collection(Const.notifCollection)
                    .add(notification)
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


    fun deleteOne(userId: String, postTime: Long) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val collection = db.collection(Const.users)
                    .document(userId)
                    .collection(Const.notifCollection)

                val query = collection.whereEqualTo("postTime", postTime).get()
                query.addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        document.reference.delete().addOnSuccessListener {

                        }.addOnFailureListener { e ->
                            e.printStackTrace()
                        }
                    }
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    fun deleteAll(userId: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val collection = db.collection(Const.users)
                .document(userId)
                .collection(Const.notifCollection)

            collection.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    collection.document(document.id).delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
