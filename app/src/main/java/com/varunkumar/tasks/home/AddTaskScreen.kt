package com.varunkumar.tasks.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.varunkumar.tasks.models.Task
import com.varunkumar.tasks.models.TaskCategoryColor
import com.varunkumar.tasks.utils.formatLongToString
import com.varunkumar.tasks.utils.formatTimeToTimePickerStateToString
import com.varunkumar.tasks.utils.mapIntToColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: HomeViewModel,
    state: HomeState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "${if (state.isUpdatingTask) "Update" else "Add"} Task",
            style = MaterialTheme.typography.headlineSmall
        )

        TaskTitle(
            modifier = Modifier.fillMaxWidth(),
            task = state.task,
            onValueTitleChange = viewModel::updateTitle,
            onValueDescriptionChange = viewModel::updateDescription
        )

        TaskCategoryView(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            onUpdateTaskCategory = viewModel::updateTaskCategory
        )

        TaskButtons(
            modifier = Modifier.fillMaxWidth(),
            task = state.task,
            isUpdatingTask = state.isUpdatingTask,
            onTaskDelete = viewModel::deleteTaskFirebase,
            onUpdateTimeStamp = viewModel::updateTimeStamp,
            onDismissRequest = {
                viewModel.addTaskFirebase(state.isUpdatingTask)
            }
        )
    }
}

@Composable
private fun TaskTitle(
    modifier: Modifier = Modifier,
    task: Task,
    onValueTitleChange: (String) -> Unit,
    onValueDescriptionChange: (String) -> Unit
) {
    val textFieldColors = TextFieldDefaults.colors(
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp)),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        TextField(
            modifier = modifier,
            value = task.title,
            shape = RoundedCornerShape(5.dp),
            colors = textFieldColors,
            label = { Text(text = "Title") },
            onValueChange = onValueTitleChange
        )

        TextField(
            modifier = modifier,
            value = task.description ?: "",
            colors = textFieldColors,
            shape = RoundedCornerShape(5.dp),
            label = { Text(text = "Description") },
            onValueChange = onValueDescriptionChange
        )
    }
}

@Composable
private fun TaskCategoryView(
    modifier: Modifier = Modifier,
    state: HomeState,
    onUpdateTaskCategory: (String) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            modifier = modifier,
            value = state.task.category?.tag ?: "",
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            label = { Text(text = "Category (Optional)") },
            onValueChange = onUpdateTaskCategory
        )

        if (state.task.category != null && state.task.category.tag.isNotEmpty()) {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TaskCategoryColor.COLORS.forEach {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(it)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskButtons(
    modifier: Modifier = Modifier,
    task: Task,
    isUpdatingTask: Boolean,
    onTaskDelete: () -> Unit,
    onDismissRequest: () -> Unit,
    onUpdateTimeStamp: (TimePickerState) -> Unit
) {
    var showTimeAlert by remember {
        mutableStateOf(false)
    }

    if (showTimeAlert) {
        TimeAlert(
            modifier = modifier
                .clip(RoundedCornerShape(20.dp)),
            onAddTimeButtonClick = onUpdateTimeStamp,
            onDismissRequest = { showTimeAlert = false }
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            onClick = { showTimeAlert = true }
        ) {
            if (task.scheduledTime != null) {
                Text(text = formatLongToString(task.scheduledTime))
            } else {
                Icon(
                    imageVector = Icons.Outlined.MoreTime,
                    contentDescription = "Add Time"
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isUpdatingTask) {
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = onTaskDelete
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription =  null
                    )
                }
            }

            Button(
                onClick = {
                    if (task.title.isNotBlank() || task.title.isNotEmpty()) onDismissRequest()
                }
            ) {
                Text(text = if (isUpdatingTask) "Update" else "Add")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeAlert(
    modifier: Modifier = Modifier,
    onAddTimeButtonClick: (TimePickerState) -> Unit,
    onDismissRequest: () -> Unit
) {
    val timeStartState = rememberTimePickerState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                modifier = modifier,
                textAlign = TextAlign.Center,
                text = "Task Time"
            )
        },
        text = {
            TimePicker(state = timeStartState)
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddTimeButtonClick(timeStartState)
                    onDismissRequest()
                }
            ) {
                Text(text = "Done")
            }
        }
    )
}