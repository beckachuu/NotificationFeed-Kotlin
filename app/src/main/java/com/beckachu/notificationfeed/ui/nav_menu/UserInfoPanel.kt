package com.beckachu.notificationfeed.ui.nav_menu

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.beckachu.notificationfeed.Const
import com.beckachu.notificationfeed.R
import com.beckachu.notificationfeed.ui.sign_in.SignInState
import com.beckachu.notificationfeed.ui.sign_in.UserData

@Composable
fun UserInfoPanel(
    state: SignInState,
    onSignInClick: () -> Unit,
    user: UserData?,
    modifier: Modifier
) {
    Spacer(modifier = Modifier.height(Const.VERTICAL_PADDING))

    if (state.isSignInSuccessful) {
        Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
            if (user != null) {
                AsyncImage(
                    model = user.profilePictureUrl,
                    contentDescription = "Profile pic",
                    modifier = Modifier
                        .padding(start = Const.LEFT_PADDING)
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${user.username}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(start = Const.LEFT_PADDING)
                )
            }
        }
    } else if (state.signInError == null) {
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
            Button(onClick = onSignInClick) {
                Text("Log In")
            }
        }
    } else {
        val context = LocalContext.current
        Toast.makeText(context, state.signInError, Toast.LENGTH_LONG).show()
    }

    Spacer(modifier = Modifier.height(16.dp))
}
