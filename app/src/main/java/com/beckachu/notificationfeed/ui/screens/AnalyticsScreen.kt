package com.beckachu.notificationfeed.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.beckachu.notificationfeed.Const

@Composable
fun AnalyticsScreen() {
    Column(modifier = Modifier.padding(Const.LEFT_PADDING)) {
        Text(text = "Welcome to the Analytics screen!")
        Button(onClick = { /* Handle button click */ }) {
            Text("Analyze!")
        }
    }
}

