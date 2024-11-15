package com.varunkumar.tasks.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.varunkumar.tasks.home.components.TopBar
import com.varunkumar.tasks.models.Task
import com.varunkumar.tasks.models.TaskCategory
import com.varunkumar.tasks.sign_in.UserData
import com.varunkumar.tasks.utils.formatLongToString
import com.varunkumar.tasks.utils.mapIntToColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    user: UserData,
    onSignOutRequest: () -> Unit
) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val categoryState by viewModel.categoryState.collectAsStateWithLifecycle()
    val homeState by viewModel.homeState.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    var showAlert by remember {
        mutableStateOf(false)
    }

    var showAccountAlert by remember {
        mutableStateOf(false)
    }

    if (showAlert) {
        FilterAlert(
            state = categoryState,
            viewModel = viewModel,
            onDismissRequest = {
                showAlert = !showAlert
            }
        )
    }

    if (homeState.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.updateBottomSheet(false) }
        ) {
            AddTaskScreen(
                viewModel = viewModel,
                state = homeState
            )
        }
    }

    if (showAccountAlert) {
        AccountAlert(
            user = user,
            onDismissRequest = { showAccountAlert = false },
            onSignOutRequest = onSignOutRequest
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                onProfileClick = {
                    showAccountAlert = true
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = {
                    viewModel.updateIsUpdatingTask(false)
                    viewModel.updateBottomSheet(true)
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                value = searchQuery,
                placeholder = { Text(text = "Search tasks") },
                onValueChange = viewModel::updateSearchQuery
            )

            TaskItemsContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(16.dp),
                tasks = tasks,
                selectedCategory = categoryState.selectedCategory,
                onCategoryChangeClick = { showAlert = !showAlert },
                onTaskItemClick = {
                    viewModel.updateTask(it)
                },
                onTaskStatusClick = {
                    viewModel.updateTaskStatusFirebase(it)
                },
                onUpdateTaskCategory = {
                    viewModel.updateSelectedCategory(it)
                }
            )
        }
    }
}

@Composable
private fun TaskItemsContainer(
    modifier: Modifier = Modifier,
    tasks: List<Task>,
    selectedCategory: TaskCategory,
    onCategoryChangeClick: () -> Unit,
    onTaskItemClick: (Task) -> Unit,
    onTaskStatusClick: (Task) -> Unit,
    onUpdateTaskCategory: (TaskCategory) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedCategory.tag,
                style = MaterialTheme.typography.bodyLarge
            )

            Icon(
                modifier = Modifier
                    .clickable { onCategoryChangeClick() },
                imageVector = Icons.Outlined.FilterAlt,
                contentDescription = null
            )
        }

        if (tasks.isEmpty()) {
            ListItem(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp)),
                headlineContent = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        text = "There are no tasks."
                    )
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
            ) {
                itemsIndexed(tasks) { index, task ->
                    TaskItem(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .clickable { onTaskItemClick(task) },
                        task = task,
                        showCategory = selectedCategory == TaskCategory("All"),
                        onTaskStatusClick = { onTaskStatusClick(task) },
                        updateSelectedCategory = { onUpdateTaskCategory(it) }
                    )

                    if (index != tasks.lastIndex) Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    modifier: Modifier = Modifier,
    showCategory: Boolean = false,
    task: Task,
    onTaskStatusClick: () -> Unit,
    updateSelectedCategory: (TaskCategory) -> Unit
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(
                text = task.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.status) TextDecoration.LineThrough else TextDecoration.None,
                fontWeight = FontWeight.Bold
            )
        },
        supportingContent = {
            Column {
                task.description?.let {
                    Text(
                        text = it,
                        textDecoration = if (task.status) TextDecoration.LineThrough else TextDecoration.None
                    )
                }

                task.scheduledTime?.let {
                    Spacer(modifier = Modifier.height(5.dp))

                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TagRow(
                            content = {
                                Icon(
                                    modifier = Modifier.size(15.dp),
                                    imageVector = Icons.Outlined.AccessTime,
                                    contentDescription = null
                                )

                                Text(text = formatLongToString(it))
                            }
                        )

                        if (showCategory && task.category != null) {
                            TagRow(
                                modifier = Modifier
                                    .clickable { updateSelectedCategory(task.category) },
                                content = {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(mapIntToColor(task.category.color))
                                    )

                                    Text(text = task.category.tag)
                                }
                            )
                        }
                    }
                }
            }
        },
        leadingContent = {
            RadioButton(
                modifier = Modifier
                    .size(20.dp),
                selected = task.status,
                onClick = onTaskStatusClick
            )
        }
    )
}

@Composable
private fun TagRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(vertical = 5.dp, horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
private fun AccountAlert(
    user: UserData,
    onSignOutRequest: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp)),
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = user.username ?: "null"
            )
        },
        text = {
            Row {
                Icon(imageVector = Icons.Outlined.Error, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Are you sure you want to sign out of ${user.username}! You can still recover all the tasks after signing in again.")
            }

        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = onSignOutRequest) {
                Text(text = "Sign Out")
            }
        }
    )
}

//@Composable
//private fun ResultStatus(
//    modifier: Modifier = Modifier,
//    context: Context,
//    result: Result<Boolean>
//) {
//    when (result) {
//        is Result.Loading -> {
//            CircularProgressIndicator(
//                modifier = modifier,
//                color = MaterialTheme.colorScheme.primary
//            )
//        }
//
//        else -> {
//            Toast.makeText(context, result.message.toString(), Toast.LENGTH_SHORT).show()
//        }
//    }
//
//}

@Composable
private fun FilterAlert(
    state: CategoryState,
    viewModel: HomeViewModel,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp)),
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Filter Tasks"
            )
        },
        text = {
            Column {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Filter Tasks on the basis of task category."
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalStaggeredGrid(
                    modifier = Modifier.fillMaxWidth(),
                    columns = StaggeredGridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalItemSpacing = 2.dp
                ) {
                    state.categories.forEach { task ->
                        item {
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (state.selectedCategory != task)
                                        MaterialTheme.colorScheme.secondaryContainer
                                    else MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                onClick = { viewModel.updateSelectedCategory(task) },
                            ) {
                                Text(text = task.tag)
                            }
                        }
                    }
                }
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = onDismissRequest) {
                Text(text = "Done")
            }
        }
    )
}