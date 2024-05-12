package com.beckachu.notificationfeed.ui.notification

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.beckachu.notificationfeed.data.entities.NotificationEntity
import com.beckachu.notificationfeed.utils.Util
import com.beckachu.notificationfeed.utils.Util.toImageBitmap
import java.time.Instant
import java.time.ZoneId


@Composable
fun NotificationModelList(notifications: LazyPagingItems<NotificationEntity>, context: Context) {
    val groupedNotifications = notifications.itemSnapshotList.items
        .groupBy { notification ->
            val date = Instant.ofEpochMilli(notification.postTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            date.format(Util.dateFormat)
        }
        .toSortedMap(compareByDescending { it })

    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
        groupedNotifications.forEach { (date, notifs) ->
            items(notifs.size) { index ->

                if (notifs[index] == notifs.first()) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                }

                NotificationModelCard(notifs[index], context)
            }
        }
    }
}


@Composable
fun NotificationModelCard(notification: NotificationEntity?, context: Context) {
    var extended by remember { mutableStateOf(false) }

    if (notification != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { extended = !extended },
            elevation = CardDefaults.elevatedCardElevation()
        ) {
            Column(
                modifier = Modifier
                    .animateContentSize()
                    .padding(12.dp)
            ) {
                val appIcon = Util.getAppIconFromPackage(context, notification.packageName)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    appIcon?.toImageBitmap()?.let {
                        Image(
                            bitmap = it,
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = if (extended) Int.MAX_VALUE else 1
                    )
                }

                val text = notification.text
                val textBig = notification.textBig

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge
                )

                if (extended && (textBig != "") && (text != textBig)) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = textBig,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

