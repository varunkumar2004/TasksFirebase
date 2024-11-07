package com.varunkumar.tasks

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.varunkumar.tasks.sign_in.UserData
import com.varunkumar.tasks.home.HomeScreen
import com.varunkumar.tasks.notification.TaskSchedulerService
import com.varunkumar.tasks.sign_in.GoogleAuthUiClient
import com.varunkumar.tasks.sign_in.SignInScreen
import com.varunkumar.tasks.ui.theme.TasksFirebaseTheme
import com.varunkumar.tasks.utils.HomeScreenRoute
import com.varunkumar.tasks.utils.SignInScreen
import com.varunkumar.tasks.sign_in.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var googleAuthUiClient: GoogleAuthUiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            var hasNotificationPermission by rememberSaveable { mutableStateOf(false) }
            var hasExactAlarmPermission by rememberSaveable { mutableStateOf(false) }

            val permissionLauncher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    hasNotificationPermission = isGranted
                }

            val navController = rememberNavController()

            val signInViewModel = hiltViewModel<SignInViewModel>()

            TasksFirebaseTheme {
                val signState by signInViewModel.state.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    val user = googleAuthUiClient.getSignedInUser()

                    user?.let {
                        navController.navigate(HomeScreenRoute)
                    }
                }

                LaunchedEffect(Unit) {
                    checkAndRequestNotificationPermission(
                        applicationContext,
                        permissionLauncher,
                        hasNotificationPermission = hasNotificationPermission,
                        notificationPermissionStatus = { permission ->
                            hasNotificationPermission = permission
                        }
                    )

                    checkAndRequestExactAlarmPermission(
                        applicationContext,
                        alarmPermissionStatus = { permission ->
                            hasExactAlarmPermission = permission
                        }
                    )
                }

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

                LaunchedEffect(key1 = signState.isSignInSuccessful) {
                    if (signState.isSignInSuccessful) {
                        Toast.makeText(
                            applicationContext,
                            "Sign in successful",
                            Toast.LENGTH_LONG
                        ).show()

                        navController.navigate(HomeScreenRoute)
                        signInViewModel.resetState()
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = SignInScreen
                ) {
                    composable<HomeScreenRoute> {
                        HomeScreen(
                            user = googleAuthUiClient.getSignedInUser() ?: UserData(),
                            onSignOutRequest = {
                                lifecycleScope.launch {
                                    navController.navigate(SignInScreen)
                                    googleAuthUiClient.signOut()
                                }
                            }
                        )
                    }

                    composable<SignInScreen> {
                        SignInScreen(
                            state = signState,
                            onSignInClick = {
                                lifecycleScope.launch {
                                    val signInIntentSender = googleAuthUiClient.signIn()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}



private fun checkAndRequestExactAlarmPermission(
    context: Context,
    alarmPermissionStatus: (Boolean) -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Direct users to system settings to enable exact alarm permission
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (!alarmManager.canScheduleExactAlarms()) context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
        else alarmPermissionStatus(true)
    } else alarmPermissionStatus(true)
}

private fun checkAndRequestNotificationPermission(
    context: Context,
    permissionLauncher: ActivityResultLauncher<String>,
    hasNotificationPermission: Boolean,
    notificationPermissionStatus: (Boolean) -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionCheckResult = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        )

        notificationPermissionStatus(permissionCheckResult == PackageManager.PERMISSION_GRANTED)

        if (!hasNotificationPermission)
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    } else notificationPermissionStatus(true)
}