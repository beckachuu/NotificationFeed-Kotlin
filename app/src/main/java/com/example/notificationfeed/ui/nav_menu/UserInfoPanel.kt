package com.example.notificationfeed.ui.nav_menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.notificationfeed.Const
import com.example.notificationfeed.R

@Composable
fun UserInfoPanel(isLoggedIn: Boolean, modifier: Modifier) {
    Spacer(modifier = Modifier.height(Const.VERTICAL_PADDING))

    if (isLoggedIn) {
        Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
            Image(
                painterResource(R.drawable.ic_user),
                contentDescription = "User Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            Text(text = "Username", style = MaterialTheme.typography.headlineSmall)
        }
    } else {
        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painterResource(R.drawable.ic_user),
                contentDescription = "Guest User"
            )
            Text(text = "Hello, Guest", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Log in to unlock more features!",
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* Navigate to login screen */ }) {
                Text("Log In")
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}
