package com.varunkumar.tasks.home

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.FilterAltOff
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.varunkumar.tasks.models.Task
import com.varunkumar.tasks.models.TaskCategory
import com.varunkumar.tasks.sign_in.UserData
import com.varunkumar.tasks.home.components.TopBar
import com.varunkumar.tasks.utils.formatLongToString

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    user: UserData,
    onSignOutRequest: () -> Unit
) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val resultState by viewModel.resultState.collectAsStateWithLifecycle()
    val categoryState by viewModel.categoryState.collectAsStateWithLifecycle()
    val homeState by viewModel.homeState.collectAsStateWithLifecycle()
    val context = LocalContext.current


    var showAlert by remember {
        mutableStateOf(false)
    }

    var showAccountAlert by remember {
        mutableStateOf(false)
    }

//    ResultStatus(
//        modifier = Modifier
//            .fillMaxSize(),
//        context = context,
//        result = resultState
//    )

    if (showAlert) {
        FilterAlert(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceBright),
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
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer),
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
                leadingIcon = {
                    IconButton(
                        modifier = Modifier
                            .height(TextFieldDefaults.MinHeight),
                        onClick = { showAlert = !showAlert }
                    ) {
                        Icon(
                            imageVector =
                            if (categoryState.selectedCategory != TaskCategory("All"))
                                Icons.Outlined.FilterAlt
                            else Icons.Outlined.FilterAltOff,
                            contentDescription = "null"
                        )
                    }
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null
                    )
                },
                placeholder = { Text(text = "Search") },
                onValueChange = viewModel::updateSearchQuery
            )

            TaskCategoriesView(
                modifier = Modifier.fillMaxWidth(),
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun TaskCategoriesView(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
    ) {
        itemsIndexed(tasks) { index, item ->
            TaskItem(
                modifier = modifier
                    .clip(RoundedCornerShape(5.dp))
                    .clickable {
                        viewModel.updateTask(item)
                    }
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(16.dp),
                viewModel = viewModel,
                task = item
            )

            if (index != tasks.lastIndex) Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
private fun TaskItem(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    task: Task
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Log.d("task received completion", task.status.toString())

        RadioButton(
            modifier = Modifier
                .size(20.dp),
            selected = task.status,
            onClick = {
                viewModel.updateTaskStatusFirebase(task = task)
            }
        )

        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = task.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.status) TextDecoration.LineThrough else TextDecoration.None,
                fontWeight = FontWeight.Bold
            )

            task.description?.let {
                Text(
                    text = task.description,
                    textDecoration = if (task.status) TextDecoration.LineThrough else TextDecoration.None,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            task.category?.let {
                Text(
                    text = "#${it.tag}",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        task.scheduledTime?.let { time ->
            Text(
                text = formatLongToString(time),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun AccountAlert(
    modifier: Modifier = Modifier,
    user: UserData,
    onSignOutRequest: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterAlert(
    modifier: Modifier = Modifier,
    state: CategoryState,
    viewModel: HomeViewModel,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
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

                Spacer(modifier = Modifier.height(5.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    state.categories.forEach { item ->
                        FilterChip(
                            selected = state.selectedCategory == item,
                            onClick = { viewModel.updateSelectedCategory(item) },
                            label = { Text(text = item.tag) }
                        )
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