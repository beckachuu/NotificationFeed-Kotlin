package com.beckachu.notificationfeed.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.beckachu.notificationfeed.data.entities.AppEntity
import com.beckachu.notificationfeed.domain.notif.NotificationListener
import com.beckachu.notificationfeed.navigation.NavGraph
import com.beckachu.notificationfeed.ui.dialogs.PermissionDialog
import com.beckachu.notificationfeed.ui.nav_menu.NavigationPane
import com.beckachu.notificationfeed.ui.sign_in.GoogleAuthUIClient
import com.beckachu.notificationfeed.ui.theme.NotifFeedTheme
import com.beckachu.notificationfeed.ui.viewmodels.AppListViewModel
import com.beckachu.notificationfeed.ui.viewmodels.NotifListViewModel
import com.beckachu.notificationfeed.ui.viewmodels.PermissionViewModel
import com.beckachu.notificationfeed.ui.viewmodels.SignInViewModel
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val permissionViewModel: PermissionViewModel by viewModels()
    private val googleAuthUiClient by lazy {
        GoogleAuthUIClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotifFeedTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    startService(Intent(this, NotificationListener::class.java))
                    AskPermission()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setContent {
            NotifFeedTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AskPermission()
                }
            }
        }
    }


    @Composable
    fun AskPermission() {
        if (!permissionViewModel.isNotificationServiceEnabled()) {
            PermissionDialog(
                dialogText = "You need to enable the permission to use this app. Please enable the Notification access permission to make the app work.",
                onConfirm = permissionViewModel::openNotificationAccessSettings
            )
        } else {
            MainLayout()
        }
    }


    @Composable
    fun MainLayout() {
        val drawerState = rememberDrawerState(DrawerValue.Closed)

        val appListViewModel: AppListViewModel = hiltViewModel()
        val appList = appListViewModel.appList.observeAsState(emptyList<AppEntity>())

        val signInViewModel: SignInViewModel by viewModels()
        val navController = rememberNavController()
        NavGraph(navController)

        val notifListViewModel: NotifListViewModel = hiltViewModel()

        val state by signInViewModel.state.collectAsStateWithLifecycle()
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { result ->
                if (result.resultCode == RESULT_OK) {
                    lifecycleScope.launch {
                        val signInResult = googleAuthUiClient.signInWithIntent(
                            intent = result.data ?: return@launch
                        )
                        signInViewModel.onSignInResult(signInResult)
                    }
                }
            }
        )

        NavigationPane(
            drawerState,
            appList.value,
            navController,
            notifListViewModel,
            state,
            onSignInClick = {
                lifecycleScope.launch {
                    val signInIntentSender = googleAuthUiClient.signIn()
                    launcher.launch(
                        IntentSenderRequest.Builder(
                            signInIntentSender ?: return@launch
                        ).build()
                    )
                }
            },
            googleAuthUiClient.getSignedInUser(),
            applicationContext
        )

    }
}
