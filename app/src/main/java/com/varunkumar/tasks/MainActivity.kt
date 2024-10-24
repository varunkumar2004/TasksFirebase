package com.varunkumar.tasks

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.varunkumar.tasks.screens.AddTaskScreen
import com.varunkumar.tasks.screens.HomeScreen
import com.varunkumar.tasks.ui.theme.TasksFirebaseTheme
import com.varunkumar.tasks.utils.CalendarScreen
import com.varunkumar.tasks.utils.HomeScreen
import com.varunkumar.tasks.utils.ScreenRoute
import com.varunkumar.tasks.viewmodels.HomeViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.selects.select

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val screenModifier = Modifier
                .fillMaxSize()

            var showBottomSheet by remember { mutableStateOf(false) }
            val navController = rememberNavController()
            var selectedRoute by remember {
                mutableStateOf<ScreenRoute>(HomeScreen)
            }
            val homeViewmodel = hiltViewModel<HomeViewmodel>()

            TasksFirebaseTheme {
                if (showBottomSheet) {
                    ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                        AddTaskScreen(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 20.dp),
                            onDismissRequest = { showBottomSheet = false }
                        )
                    }
                }

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    contentColor = MaterialTheme.colorScheme.secondary,
                    modifier = screenModifier,
                    topBar = {
                        TopBar(
                            modifier = Modifier
                                .fillMaxWidth(),
                            selectedRoute = selectedRoute
                        )
                    },
                    bottomBar = {
                        BottomNavigation(
                            modifier = Modifier
                                .fillMaxWidth(),
                            selectedRoute = selectedRoute,
                            onClick = { route ->
                                selectedRoute = route
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
                                    .padding(innerPadding)
                            )
                        }

//                        composable<> {  }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCategoriesView(
    modifier: Modifier = Modifier,
    tasks: List<String>
) {
    LazyColumn(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))

    ) {
        itemsIndexed(tasks) { index, item ->
            TaskItem(
                modifier = modifier
                    .padding(16.dp),
                task = item
            )

            if (index != tasks.lastIndex) HorizontalDivider()
        }
    }
}

@Composable
private fun TaskItem(
    modifier: Modifier = Modifier,
    task: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RadioButton(
            modifier = Modifier
                .size(20.dp),
            selected = false,
            onClick = { /*TODO*/ }
        )

        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = task,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "lashd fklahs dklfj hakljsdh fiuae hfklajsdh",
                style = MaterialTheme.typography.bodySmall
            )
        }

//        I/*conButton(onClick = { *//*TODO*//* }) {
//            Icon(imageVector = Icons.Outlined.AddCircle, contentDescription = null)
//        }*/
    }

//    ListItem(
//        modifier = modifier,
//        colors = ListItemDefaults.colors(
//            containerColor = MaterialTheme.colorScheme.onSecondary
//        ),
//        headlineContent = {
//            Text(
//                text = task,
//                style = MaterialTheme.typography.bodyLarge
//            )
//        },
//        supportingContent = {
//            Text(
//                text = "lashd fklahs dklfj hakljsdh fiuae hfklajsdh",
//                style = MaterialTheme.typography.bodySmall
//            )
//        },
//        leadingContent = {
//            RadioButton(
//                selected = false,
//                onClick = { /*TODO*/ }
//            )
//        }
//    )
}

@Composable
private fun ImagePicker(modifier: Modifier = Modifier) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
//            containerColor = MaterialTheme.colorScheme.
        ),
        shape = RoundedCornerShape(10.dp),
        onClick = {
            launcher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts
                        .PickVisualMedia
                        .ImageOnly
                )
            )
        }
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null && selectedImageUri != Uri.EMPTY) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = null
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null
                    )
                    Text(
                        text = "Add Images",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    selectedRoute: ScreenRoute
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = selectedRoute.route)
        },
        modifier = modifier,
        actions = {
            ProfileImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
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
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            }
        )

        NavigationBarItem(
            selected = selectedRoute == CalendarScreen,
            onClick = { onClick(CalendarScreen) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = "Home"
                )
            }
        )
    }
}

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier)
}