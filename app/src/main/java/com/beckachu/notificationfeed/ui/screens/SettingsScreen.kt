package com.beckachu.notificationfeed.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.media.AudioManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.beckachu.notificationfeed.Const
import com.beckachu.notificationfeed.data.SharedPrefsManager
import com.beckachu.notificationfeed.data.di.NotifRepoModule
import com.beckachu.notificationfeed.data.entities.AppEntity
import com.beckachu.notificationfeed.data.repositories.NotificationRepositoryImpl
import com.beckachu.notificationfeed.domain.noise.NoiseListenerWorker
import com.beckachu.notificationfeed.ui.components.LongDivider
import com.beckachu.notificationfeed.ui.components.ProgressBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(appList: List<AppEntity?>?) {
    val context = LocalContext.current
    val sharedPref =
        context.getSharedPreferences(SharedPrefsManager.DEFAULT_NAME, Context.MODE_PRIVATE)

    var autoAdjustVolume by remember { mutableStateOf(false) }
    autoAdjustVolume = SharedPrefsManager.getBool(sharedPref, SharedPrefsManager.AUTO_VOLUME, false)

    val checkedAppList by rememberUpdatedState(
        SharedPrefsManager.getStringSet(sharedPref, SharedPrefsManager.APP_LIST)
    )
    var recordChecked by remember { mutableStateOf(false) }
    recordChecked = SharedPrefsManager.getBool(sharedPref, SharedPrefsManager.RECORD_CHECKED, false)

    var checkNewApp by remember { mutableStateOf(false) }
    checkNewApp = SharedPrefsManager.getBool(sharedPref, SharedPrefsManager.CHECK_NEW_APP, false)

    val confirmDeleteAllDialog = remember { mutableStateOf(false) }

    val notificationRepositoryImpl: NotificationRepositoryImpl =
        NotifRepoModule.provideNotifRepository(context)

    // Volume bar
    val scope = rememberCoroutineScope()
    var progressBarColor by remember { mutableStateOf(Color.Gray) }
    var currentVolume by remember { mutableFloatStateOf(0f) }

    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val maxSystemVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    DisposableEffect(Unit) {
        val job = scope.launch {
            while (isActive) {
                val systemVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                currentVolume = systemVolume.toFloat() / maxSystemVolume
                delay(1000)
            }
        }

        onDispose {
            job.cancel()
        }
    }

    Column(
        modifier = Modifier.padding(
            Const.LEFT_PADDING,
            4.dp,
            Const.LEFT_PADDING,
            Const.LEFT_PADDING
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = {
                    val userId =
                        SharedPrefsManager.getString(sharedPref, SharedPrefsManager.USER_ID, null)
                    notificationRepositoryImpl.pullAndSave(context, userId)
                })
                .height(Const.ROW_HEIGHT),
        ) {
            Text(
                text = "Pull from online storage",
                style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary)
            )
        }

//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .clickable(onClick = {
//                    val userId =
//                        SharedPrefsManager.getString(sharedPref, SharedPrefsManager.USER_ID, null)
//                    notificationRepositoryImpl.pushAllToRemote(userId)
//                })
//                .height(Const.ROW_HEIGHT),
//        ) {
//            Text(
//                text = "Push to online storage",
//                style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary)
//            )
//        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = { confirmDeleteAllDialog.value = true })
                .height(Const.ROW_HEIGHT),
        ) {
            Text(
                text = "Delete ALL",
                style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.error)
            )
        }
        if (confirmDeleteAllDialog.value) {
            AlertDialog(
                onDismissRequest = { confirmDeleteAllDialog.value = false },
                title = { Text("Confirm Deletion") },
                text = {
                    Text(
                        text = "Where do you want to delete all data? (Click outside this dialog to cancel)",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            notificationRepositoryImpl.deleteAllFromLocal()
                            confirmDeleteAllDialog.value = false
                        },
                    ) {
                        Text(
                            text = "Local",
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            val userId =
                                SharedPrefsManager.getString(
                                    sharedPref,
                                    SharedPrefsManager.USER_ID,
                                    null
                                )
                            notificationRepositoryImpl.deleteAllFromRemote(userId)
                            confirmDeleteAllDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = "Online",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(Const.BIG_ROW_HEIGHT)
        ) {
            Text(
                text = "Auto-adjust volume",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
            )
            Switch(
                checked = autoAdjustVolume,
                onCheckedChange = { isChecked ->
                    autoAdjustVolume = isChecked
                    SharedPrefsManager.putBool(
                        sharedPref,
                        SharedPrefsManager.AUTO_VOLUME,
                        isChecked
                    )

                    println("Auto-adjust volume: $isChecked")

                    progressBarColor = if (isChecked) {
                        val workRequest = OneTimeWorkRequestBuilder<NoiseListenerWorker>()
                            .addTag("noise_listener_worker_tag")
                            .build()
                        WorkManager.getInstance(context)
                            .enqueue(workRequest)

                        Color(0xFF315DA8)
                    } else {
                        WorkManager.getInstance(context)
                            .cancelAllWorkByTag("NoiseListenerUniqueWork")

                        Color.Gray
                    }
                }
            )
        }

        // Volume level indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            ProgressBar(
                backgroundColor = progressBarColor,
                progress = currentVolume,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(17.dp)
            )
        }

        LongDivider()


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(Const.BIG_ROW_HEIGHT)
        ) {
            Text(
                text = "Enable to include, disable to exclude",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            )
            Switch(
                checked = recordChecked,
                onCheckedChange = { isChecked ->
                    run {
                        recordChecked = isChecked

                        SharedPrefsManager.putBool(
                            sharedPref,
                            SharedPrefsManager.RECORD_CHECKED,
                            isChecked
                        )
                    }
                }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(Const.BIG_ROW_HEIGHT)
        ) {
            Text(
                text = "Check new app by default",
                modifier = Modifier.weight(1f),
//                style = MaterialTheme.typography.bodyLarge,
            )
            Switch(
                checked = checkNewApp,
                onCheckedChange = { isChecked ->
                    run {
                        checkNewApp = isChecked

                        SharedPrefsManager.putBool(
                            sharedPref,
                            SharedPrefsManager.CHECK_NEW_APP,
                            isChecked
                        )
                    }
                }
            )
        }



        LazyColumn {
            items(appList?.size ?: 0) { index ->
                val app = appList?.get(index)
                val appName = app?.appName
                val packageName = app?.packageName
                val isChecked = remember { mutableStateOf(checkedAppList.contains(packageName)) }

                Row(
                    modifier = Modifier
                        .height(Const.ROW_HEIGHT)
                        .clickable(onClick = {
                            isChecked.value = !isChecked.value
                            if (isChecked.value) {
                                if (packageName != null) {
                                    checkedAppList.add(packageName)
                                }
                            } else {
                                checkedAppList.remove(packageName)
                            }
                            sharedPref
                                .edit()
                                .putStringSet(SharedPrefsManager.APP_LIST, checkedAppList)
                                .apply()
                        })
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val appIcon =
                        app?.iconByte?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
                    if (appIcon != null) {
                        Image(
                            bitmap = appIcon.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    if (appName != null) {
                        Text(
                            text = appName,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Checkbox(
                        checked = isChecked.value,
                        onCheckedChange = null
                    )
                }
            }
        }
    }
}

