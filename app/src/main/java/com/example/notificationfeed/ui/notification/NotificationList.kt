package com.example.notificationfeed.ui.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.notificationfeed.data.entities.NotificationEntity

@Composable
fun NotificationModelList(notifications: List<NotificationEntity?>) {
    LazyColumn {
        items(notifications) { notification ->
            if (notification != null) {
                NotificationModelCard(notification)
            }
        }
    }
}

@Composable
fun NotificationModelCard(notification: NotificationEntity) {
    var extended by remember { mutableStateOf(false) }

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
