package com.beckachu.notificationfeed.ui.notification

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.beckachu.notificationfeed.data.SharedPrefsManager
import com.beckachu.notificationfeed.data.entities.NotificationEntity
import com.beckachu.notificationfeed.data.repositories.NotificationRepositoryImpl
import com.beckachu.notificationfeed.utils.Util
import com.beckachu.notificationfeed.utils.Util.toImageBitmap
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


@Composable
fun NotificationModelList(
    notifications: LazyPagingItems<NotificationEntity>,
    screenWidth: Dp,
    notificationRepositoryImpl: NotificationRepositoryImpl,
    context: Context
) {
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

                NotificationModelCard(
                    notifs[index],
                    screenWidth,
                    notificationRepositoryImpl,
                    context
                )
            }
        }
    }
}


@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun NotificationModelCard(
    notification: NotificationEntity?,
    screenWidth: Dp,
    notificationRepositoryImpl: NotificationRepositoryImpl,
    context: Context
) {
    val sharedPref =
        context.getSharedPreferences(SharedPrefsManager.DEFAULT_NAME, Context.MODE_PRIVATE)
    var extended by remember { mutableStateOf(false) }

    if (notification != null) {
        val anchors =
            mapOf(0f to false, -300f to true)
        val swipeableState = rememberSwipeableState(initialValue = false)

        Box(
            Modifier
                .width(screenWidth * 2)
                .fillMaxHeight()
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(0.5f) },
                    orientation = Orientation.Horizontal
                )
        ) {
            // Underlying buttons
            Row(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {
                    val userId =
                        SharedPrefsManager.getString(
                            sharedPref,
                            SharedPrefsManager.USER_ID,
                            null
                        )
                    notificationRepositoryImpl.trashOrDelete(
                        notification.expireTime != null,
                        notification.postTime,
                        userId
                    )
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
                IconButton(onClick = {
                    Util.openApp(context, notification.packageName)
                }) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Open App")
                }
            }

            // Notification card
            Card(
                modifier = Modifier
                    .width(screenWidth)
                    .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
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

                        val postTime = Instant.ofEpochMilli(notification.postTime)
                        val formattedTime = postTime.atZone(ZoneId.systemDefault()).format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = notification.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = if (extended) Int.MAX_VALUE else 1,
                                modifier = Modifier.weight(1f),
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formattedTime,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }

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
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = if (extended) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

