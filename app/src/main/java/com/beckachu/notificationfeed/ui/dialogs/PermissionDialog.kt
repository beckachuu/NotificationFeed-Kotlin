package com.beckachu.notificationfeed.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PermissionDialog(
    dialogText: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Permission required") },
        text = {
            Text(
                text = dialogText,
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Go to settings")
            }
        }
    )
}
