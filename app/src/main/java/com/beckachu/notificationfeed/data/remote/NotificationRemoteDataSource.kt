package com.beckachu.notificationfeed.data.remote

import com.beckachu.notificationfeed.Const
import com.beckachu.notificationfeed.data.entities.NotificationEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationRemoteDataSource(firebaseDb: FirebaseFirestore) {
    private val db: FirebaseFirestore = firebaseDb

    fun pushToRemote(userId: String, notificationEntity: NotificationEntity) =
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

    fun removeFromRemote(userId: String, notificationEntity: NotificationEntity) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection(Const.users)
                    .document(userId)
                    .collection(Const.notifCollection)
                    .add(notificationEntity)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    fun pullFromRemote(userId: String) {

    }

}
