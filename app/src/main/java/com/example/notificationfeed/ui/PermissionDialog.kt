package com.example.notificationfeed.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun PermissionDialog(onConfirm: () -> Unit) {
    var dialogText by remember { mutableStateOf("Please enable the Notification access permission to make the app work.") }

    AlertDialog(
        onDismissRequest = {
            dialogText =
                "You need to enable the permission to use this app. Please enable the Notification access permission to make the app work."
        },
        title = { Text("Permission required") },
        text = { Text(dialogText) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Go to settings")
            }
        }
    )
}
