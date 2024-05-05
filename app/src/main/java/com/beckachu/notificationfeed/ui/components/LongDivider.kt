package com.beckachu.notificationfeed.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beckachu.notificationfeed.Const

@Composable
fun LongDivider() {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Const.LEFT_PADDING, end = Const.LEFT_PADDING),
        thickness = 0.5.dp
    )
}