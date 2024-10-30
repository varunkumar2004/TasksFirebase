package com.varunkumar.tasks.screens

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.varunkumar.tasks.models.Task
import com.varunkumar.tasks.models.UserData
import com.varunkumar.tasks.presentation.components.ProfileImage
import com.varunkumar.tasks.presentation.components.TopBar
import com.varunkumar.tasks.sign_in.SignInResult
import com.varunkumar.tasks.viewmodels.HomeViewModel
import com.varunkumar.tasks.viewmodels.SignInState
import kotlin.math.sign

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    user: UserData
) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val showBottomSheet by viewModel.showModelBottomSheet.collectAsStateWithLifecycle(false)
    val task by viewModel.task.collectAsStateWithLifecycle()
    val categoryState by viewModel.categoryState.collectAsStateWithLifecycle()

    var showAccountAlert by remember {
        mutableStateOf(false)
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.updateBottomSheet(false) }
        ) {
            AddTaskScreen(
                task = task,
                viewModel = viewModel
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
            user = user
        )
    }

    Scaffold(
        topBar = {
            TopBar(
                modifier = Modifier.fillMaxWidth(),
                user = null,
                onProfileClick = {
                    showAccountAlert = true
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { viewModel.updateBottomSheet(true) }
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
            val tasks by viewModel.tasks.collectAsStateWithLifecycle(initialValue = emptyList())

            FlowRow {
                categoryState.categories.forEach { item ->
                    FilterChip(
                        selected = item == categoryState.selectedCategory,
                        onClick = { viewModel.updateSelectedCategory(item) },
                        label = { Text(text = item) }
                    )

                    Spacer(modifier = Modifier.width(5.dp))
                }
            }

            TaskCategoriesView(
                modifier = Modifier.fillMaxWidth(),
                tasks = tasks,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun TaskCategoriesView(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    tasks: List<Task>
) {
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
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            modifier = Modifier
                .size(20.dp),
            selected = task.isCompleted,
            onClick = {
                viewModel.updateTaskStatus(task = task)
            }
        )

        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = task.title,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = task.description,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                style = MaterialTheme.typography.bodySmall
            )
        }

        task.startTaskTime?.let { time ->
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountAlert(
    modifier: Modifier = Modifier,
    user: UserData,
    onDismissRequest: () -> Unit
) {
    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.Login,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileImage(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                    user = user
                )

                Text(
                    text = user.username ?: "noting",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}