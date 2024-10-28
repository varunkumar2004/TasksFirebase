package com.varunkumar.tasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.varunkumar.tasks.screens.AddTaskScreen
import com.varunkumar.tasks.screens.CalendarScreen
import com.varunkumar.tasks.screens.HomeScreen
import com.varunkumar.tasks.sign_in.GoogleAuthUiClient
import com.varunkumar.tasks.ui.theme.TasksFirebaseTheme
import com.varunkumar.tasks.utils.CalendarScreen
import com.varunkumar.tasks.utils.HomeScreen
import com.varunkumar.tasks.utils.ScreenRoute
import com.varunkumar.tasks.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var googleAuthUiClient: GoogleAuthUiClient

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val screenModifier = Modifier
                .fillMaxSize()

            var showBottomSheet by remember { mutableStateOf(false) }

            val navController = rememberNavController()
            var selectedRoute by remember { mutableStateOf<ScreenRoute>(HomeScreen) }
            val showFloatingButton by remember {
                mutableStateOf(selectedRoute == HomeScreen)
            }

            var showAccountAlert by remember { mutableStateOf(false) }

            val homeViewModel = hiltViewModel<HomeViewModel>()

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
                        onDismissRequest = { showAccountAlert = false }
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
                            selectedRoute = selectedRoute,
                            onProfileClick = { showAccountAlert = true }
                        )
                    },
                    bottomBar = {
                        BottomNavigation(
                            modifier = Modifier
                                .fillMaxWidth(),
                            selectedRoute = selectedRoute,
                            onClick = { route ->
                                selectedRoute = route
                                navController.navigate(route)
                            }
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { showBottomSheet = true },
                            shape = CircleShape
                        ) {
                            Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                        }
                    }
                ) { innerPadding ->
                    NavHost(navController = navController, startDestination = HomeScreen) {
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

                        composable<CalendarScreen> {
                            CalendarScreen(
                                modifier = screenModifier
                                    .padding(innerPadding)
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
            ProfileImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onProfileClick() }
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
    )
}

@Composable
private fun BottomNavigation(
    modifier: Modifier = Modifier,
    selectedRoute: ScreenRoute,
    onClick: (ScreenRoute) -> Unit
) {
    NavigationBar(
        modifier = modifier
    ) {
        NavigationBarItem(
            selected = selectedRoute == HomeScreen,
            onClick = { onClick(HomeScreen) },
            icon = {
                Icon(
                    imageVector =
                    if (selectedRoute == HomeScreen)
                        HomeScreen.filledIcon
                    else HomeScreen.outlinedIcon,
                    contentDescription = HomeScreen.title
                )
            }
        )

        NavigationBarItem(
            selected = selectedRoute == CalendarScreen,
            onClick = { onClick(CalendarScreen) },
            icon = {
                Icon(
                    imageVector =
                    if (selectedRoute == CalendarScreen)
                        CalendarScreen.filledIcon
                    else CalendarScreen.outlinedIcon,
                    contentDescription = CalendarScreen.title
                )
            }
        )
    }
}

@Composable
private fun ProfileImage(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
//        Icon(
//            imageVector = Icons.Outlined.PersonOutline,
//            contentDescription = "profile_img"
//        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountAlert(
    modifier: Modifier = Modifier,
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
                    .background(Color.Black)
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