package com.varunkumar.tasks

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.varunkumar.tasks.models.UserData
import com.varunkumar.tasks.screens.AddTaskScreen
import com.varunkumar.tasks.screens.HomeScreen
import com.varunkumar.tasks.sign_in.GoogleAuthUiClient
import com.varunkumar.tasks.sign_in.SignInScreen
import com.varunkumar.tasks.ui.theme.TasksFirebaseTheme
import com.varunkumar.tasks.utils.HomeScreen
import com.varunkumar.tasks.utils.ScreenRoute
import com.varunkumar.tasks.utils.SignInScreen
import com.varunkumar.tasks.viewmodels.HomeViewModel
import com.varunkumar.tasks.viewmodels.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var googleAuthUiClient: GoogleAuthUiClient

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val screenModifier = Modifier
                .fillMaxSize()

            var showBottomSheet by remember { mutableStateOf(false) }

            val navController = rememberNavController()

            var selectedRoute by remember { mutableStateOf<ScreenRoute>(SignInScreen) }

            var showAccountAlert by remember { mutableStateOf(false) }

            val homeViewModel = hiltViewModel<HomeViewModel>()
            val signInViewModel = hiltViewModel<SignInViewModel>()

            TasksFirebaseTheme {
                if (showBottomSheet) {
                    ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                        val task by homeViewModel.task.collectAsStateWithLifecycle()

                        AddTaskScreen(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 20.dp),
                            task = task,
                            viewModel = homeViewModel,
                            onDismissRequest = { showBottomSheet = false }
                        )
                    }
                }

                if (showAccountAlert) {
                    AccountAlert(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(10.dp),
                        onDismissRequest = { showAccountAlert = false },
                        user = if (selectedRoute == HomeScreen) googleAuthUiClient.getSignedInUser()!!
                        else null
                    )
                }

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = screenModifier,
                    topBar = {
                        TopBar(
                            modifier = Modifier
                                .fillMaxWidth(),
                            user = if (selectedRoute == HomeScreen) googleAuthUiClient.getSignedInUser()!! else null,
                            selectedRoute = selectedRoute,
                            onProfileClick = { showAccountAlert = true }
                        )
                    },
                    floatingActionButton = {
//                        if (selectedRoute == HomeScreen) {
                        FloatingActionButton(
                            onClick = {
                                showBottomSheet = true
                            },
                            shape = CircleShape
                        ) {
                            Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                        }
//                        }
                    }
                ) { innerPadding ->

                    val signState by signInViewModel.state.collectAsStateWithLifecycle()

//                    LaunchedEffect(Unit) {
//                        if (googleAuthUiClient.getSignedInUser() != null) {
//                            Log.d("user name", googleAuthUiClient.getSignedInUser()?.toString() ?: "nothing but a")
//                            navController.navigate(SignInScreen)
//                        }
//                    }

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

                            navController.navigate(HomeScreen)
                            selectedRoute = HomeScreen
                            signInViewModel.resetState()
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = HomeScreen
                    ) {
                        composable<HomeScreen> {
                            HomeScreen(
                                modifier = screenModifier
                                    .padding(innerPadding),
                                viewModel = homeViewModel,
                                onTaskClick = {
                                    homeViewModel.addTask(it)
                                    showBottomSheet = true
                                }
                            )
                        }

                        composable<SignInScreen> {
                            SignInScreen(
                                modifier = screenModifier
                                    .padding(innerPadding),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    selectedRoute: ScreenRoute,
    user: UserData?,
    onProfileClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            AnimatedContent(
                targetState = selectedRoute,
                label = "route title"
            ) { route ->
                Text(text = route.title)
            }
        },
        modifier = modifier,
        actions = {
            if (selectedRoute == HomeScreen) {
                ProfileImage(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() }
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                    user = user
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    )
}

@Composable
private fun ProfileImage(
    modifier: Modifier = Modifier,
    user: UserData?
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = user?.profilePictureUrl,
            contentDescription = user?.username
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountAlert(
    modifier: Modifier = Modifier,
    user: UserData?,
    onDismissRequest: () -> Unit
) {
    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileImage(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                user = user
            )

            Text(
                text = "varunkumar2004.vk@gmail.com",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Button(onClick = { /*TODO*/ }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(text = "Sign Out")
                    Icon(imageVector = Icons.Default.Login, contentDescription = null)
                }
            }
        }
    }
}