package com.beckachu.notificationfeed.ui.appbar

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beckachu.notificationfeed.ui.dialogs.PermissionDialog
import com.beckachu.notificationfeed.ui.viewmodels.NotifListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    drawerState: DrawerState, notifListViewModel: NotifListViewModel,
    context: Context
) {
    val scope = rememberCoroutineScope()
    val selectedAppName by notifListViewModel.selectedAppName.collectAsState()
    val showWarning = rememberSaveable { mutableStateOf(false) }

    var searchText by remember { mutableStateOf("") }

    var showDateRangePicker by remember { mutableStateOf(false) }
    val pickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = null,
        initialDisplayedMonthMillis = null,
        yearRange = 2023..2024,
        initialDisplayMode = DisplayMode.Picker,
    )

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        title = {
            val isSearchMode by notifListViewModel.isSearchMode.collectAsState()
            if (isSearchMode) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { newText ->
                        searchText = newText
                        notifListViewModel.searchQuery.value = newText
                    },
                    placeholder = { Text("Search notifications") },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .height(IntrinsicSize.Min),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                    )
                )
            } else {
                Text(selectedAppName ?: "All")
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    if (drawerState.isClosed) {
                        drawerState.open()
                    } else {
                        drawerState.close()
                    }
                }
            }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = { showWarning.value = true }) {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = "Warning",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            IconButton(onClick = {
                notifListViewModel.toggleSearchMode()
            }) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }

            IconButton(onClick = { showDateRangePicker = true }) {
                Icon(Icons.Filled.DateRange, contentDescription = "Filter")
            }
        },
    )


    if (showWarning.value) {
        PermissionDialog(
            dialogText = "Please enable Autostart to make the app run 24/7 (ignore this warning if you have enabled that setting for this app)",
            onConfirm = {
                val manufacturer = "xiaomi"
                if (manufacturer.equals(Build.MANUFACTURER, ignoreCase = true)) {
                    val intent = Intent(Settings.ACTION_SETTINGS).apply {
                        flags =
                            Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                        component = ComponentName(
                            "com.miui.securitycenter",
                            "com.miui.permcenter.autostart.AutoStartManagementActivity"
                        )
                    }
                    context.startActivity(intent)
                }
                showWarning.value = false
            },
            onDismissRequest = {
                showWarning.value = false
            }
        )
    }

    if (showDateRangePicker) {
        DatePickerDialog(
            modifier = Modifier
                .height(400.dp),
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        showDateRangePicker = false

                        // TODO: Show search values

                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(8.dp) // Ensure padding for visibility
                ) {
                    Text("Search", style = MaterialTheme.typography.bodyMedium)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDateRangePicker = false },
                    modifier = Modifier.padding(8.dp) // Ensure padding for visibility
                ) {
                    Text("Cancel", style = MaterialTheme.typography.bodyMedium)
                }
            }
        ) {
            DateRangePicker(state = pickerState)
        }

    }
}

