package com.beckachu.notificationfeed.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.beckachu.notificationfeed.Const

@Composable
fun TrashScreen() {
    Column(modifier = Modifier.padding(Const.LEFT_PADDING)) {
        Text(text = "Welcome to the Trash screen!")
        Icon(Icons.Default.Delete, contentDescription = "Delete icon")
    }
}

