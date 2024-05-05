package com.beckachu.notificationfeed.ui.screens

import android.content.Context
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beckachu.notificationfeed.Const
import com.beckachu.notificationfeed.data.SharedPrefsManager
import com.beckachu.notificationfeed.data.entities.AppEntity
import com.beckachu.notificationfeed.ui.components.LongDivider
import com.beckachu.notificationfeed.utils.Util
import com.beckachu.notificationfeed.utils.Util.toImageBitmap

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
                .height(Const.ROW_HEIGHT)
                .clickable(onClick = {

                })
                .fillMaxWidth()
                .height(Const.ROW_HEIGHT),
        ) {
            Text(
                text = "Pull from online storage",
                style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(Const.ROW_HEIGHT)
                .clickable(onClick = {

                })
                .fillMaxWidth()
                .height(Const.ROW_HEIGHT),
        ) {
            Text(
                text = "Push to online storage",
                style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary)
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
                    run {
                        autoAdjustVolume = isChecked

                    }
                }
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


        LazyColumn {
            items(appList?.size ?: 0) { index ->
                val appName = appList?.get(index)?.appName
                val packageName = appList?.get(index)?.packageName
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
                    val appIcon = Util.getAppIconFromPackage(context, packageName)
                    if (appIcon != null) {
                        appIcon.toImageBitmap()?.let {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                            )
                        }
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

