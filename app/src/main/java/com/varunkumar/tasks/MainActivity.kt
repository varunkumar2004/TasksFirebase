package com.varunkumar.tasks

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.varunkumar.tasks.models.UserData
import com.varunkumar.tasks.screens.HomeScreen
import com.varunkumar.tasks.sign_in.GoogleAuthUiClient
import com.varunkumar.tasks.sign_in.SignInScreen
import com.varunkumar.tasks.ui.theme.TasksFirebaseTheme
import com.varunkumar.tasks.utils.HomeScreenRoute
import com.varunkumar.tasks.utils.SignInScreen
import com.varunkumar.tasks.sign_in.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sign

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var googleAuthUiClient: GoogleAuthUiClient

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
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
                            user = googleAuthUiClient.getSignedInUser() ?: UserData()
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
