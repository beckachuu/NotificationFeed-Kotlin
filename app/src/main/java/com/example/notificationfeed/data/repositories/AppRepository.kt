package com.example.notificationfeed.data.repositories

import android.content.Context
import android.util.Log
import com.example.notificationfeed.data.entities.AppEntity
import com.example.notificationfeed.data.local.dao.AppDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val executor: ExecutorService,
    private val appDao: AppDao
) {
    fun countAllApps(): Int {
        val future: Future<Int> = executor.submit(appDao::countAll)
        return try {
            future.get()
        } catch (e: InterruptedException) {
            Log.e("AppRepository", "Error counting apps", e)
            0
        } catch (e: ExecutionException) {
            Log.e("AppRepository", "Error counting apps", e)
            0
        }
    }

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
                Log.e("AppRepository", "Error getting package names", e)
                null
            } catch (e: ExecutionException) {
                Log.e("AppRepository", "Error getting package names", e)
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
            Log.e("AppRepository", "Error updating app favorite", e)
            false
        } catch (e: ExecutionException) {
            Log.e("AppRepository", "Error updating app favorite", e)
            false
        }
    }

    fun updateReceiveNotiPref(packageName: String, pref: Boolean): Boolean {
        val future = executor.submit<Boolean> {
            return@submit appDao.setReceiveNoti(packageName, pref) != 0
        }
        return try {
            future.get()
        } catch (e: InterruptedException) {
            Log.e("AppRepository", "Error updating receive noti pref", e)
            false
        } catch (e: ExecutionException) {
            Log.e("AppRepository", "Error updating receive noti pref", e)
            false
        }
    }

    /**
     * For synchronizing remote and local data
     *
     *
     * TODO: when remote is implemented
     */
    fun pullFromRemote() {
//        val userData = networkDataSource.fetchUserData()
//        localDataSource.saveUserData(userData)
    }

    fun pushToRemote() {
//        val userData = networkDataSource.fetchUserData()
//        localDataSource.saveUserData(userData)
    }
}
