package com.beckachu.notificationfeed.data.repositories

import android.util.Log
import com.beckachu.notificationfeed.data.entities.AppEntity
import com.beckachu.notificationfeed.data.local.dao.AppDao
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppRepositoryImpl @Inject constructor(
    private val executor: ExecutorService,
    private val appDao: AppDao
) {

    val allAppByNameAsc: Flow<List<AppEntity?>> =
        appDao.allByNameAsc

    val packageNamesFromNotif: List<String?>?
        get() {
            val future: Future<List<String?>?> = executor.submit(Callable<List<String?>?> {
                appDao.packageNamesFromNotif
            })
            return try {
                future.get()
            } catch (e: InterruptedException) {
                Log.e("AppRepositoryImpl", "Error getting package names", e)
                null
            } catch (e: ExecutionException) {
                Log.e("AppRepositoryImpl", "Error getting package names", e)
                null
            }
        }

    fun addApp(myAppEntity: AppEntity) {
        executor.execute {
            appDao.insertApp(myAppEntity)
        }
    }

    fun updateAppFavorite(packageName: String, pref: Boolean): Boolean {
        val future = executor.submit<Boolean> {
            return@submit appDao.setFavorite(packageName, pref) != 0
        }
        return try {
            future.get()
        } catch (e: InterruptedException) {
            Log.e("AppRepositoryImpl", "Error updating app favorite", e)
            false
        } catch (e: ExecutionException) {
            Log.e("AppRepositoryImpl", "Error updating app favorite", e)
            false
        }
    }

    fun updateReceiveNotifPref(packageName: String, pref: Boolean): Boolean {
        val future = executor.submit<Boolean> {
            return@submit appDao.setReceiveNoti(packageName, pref) != 0
        }
        return try {
            future.get()
        } catch (e: InterruptedException) {
            Log.e("AppRepositoryImpl", "Error updating receive noti pref", e)
            false
        } catch (e: ExecutionException) {
            Log.e("AppRepositoryImpl", "Error updating receive noti pref", e)
            false
        }
    }

}

