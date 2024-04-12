package com.example.notificationfeed.ui.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.notificationfeed.data.entities.NotificationEntity

@Composable
fun NotificationModelList(notifications: LazyPagingItems<NotificationEntity>) {
    LazyColumn {
        items(count = notifications.itemCount,
            // Here we use the new itemKey extension on LazyPagingItems to
            // handle placeholders automatically, ensuring you only need to provide
            // keys for real items
            key = notifications.itemKey { it.nid },
            // Similarly, itemContentType lets you set a custom content type for each item
            contentType = notifications.itemContentType { "contentType" }) { index ->
            NotificationModelCard(notifications[index])
        }
    }
}

@Composable
fun NotificationModelCard(notification: NotificationEntity?) {
    var extended by remember { mutableStateOf(false) }

    if (notification != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { extended = !extended },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {
            Column {
//            Text(text = notification.date)
                Text(text = notification.title, maxLines = if (extended) Int.MAX_VALUE else 1)
                if (extended) {
//                Text(text = notification.preview)
                } else {
                    Text(text = notification.text)
                }
            }
        }
    }
}
