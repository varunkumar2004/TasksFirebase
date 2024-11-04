package com.varunkumar.tasks.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.MoreTime
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.varunkumar.tasks.models.Task
import com.varunkumar.tasks.utils.formatTime
import com.varunkumar.tasks.viewmodels.HomeViewModel

@Composable
fun AddTaskScreen(
    viewModel: HomeViewModel
) {
    val fModifier = Modifier.fillMaxWidth()
    val state by viewModel.homeState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            modifier = fModifier,
            text = "${if (state.isUpdatingTask) "Update" else "Add"} Task",
            style = MaterialTheme.typography.headlineSmall
        )

        TaskTitle(
            modifier = fModifier,
            task = state.task,
            onValueTitleChange = viewModel::updateTitle,
            onValueDescriptionChange = viewModel::updateDescription
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.task.taskCategory?.category ?: "",
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            label = { Text(text = "Category (Optional)") },
            onValueChange = viewModel::updateTaskCategory
        )

        TaskButtons(
            modifier = fModifier,
            task = state.task,
            viewModel = viewModel,
            isUpdatingTask = state.isUpdatingTask,
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
private fun TaskImage(
    modifier: Modifier = Modifier,
    image: String?,
    viewModel: HomeViewModel
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null && uri != Uri.EMPTY) {
            viewModel.updateImageUri(uri.toString())
        }
    }

    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        onClick = {
            launcher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    ) {
        Box(
            modifier = modifier
                .padding(16.dp)
                .heightIn(min = 100.dp, max = 100.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (image == null) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(text = "Select Image")
                } else {
                    Text(text = "Image")

                    Spacer(modifier = Modifier.width(20.dp))

                    AsyncImage(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp)),
                        model = image,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskButtons(
    modifier: Modifier = Modifier,
    task: Task,
    isUpdatingTask: Boolean,
    viewModel: HomeViewModel,
    onDismissRequest: () -> Unit
) {
    var showTimeAlert by remember {
        mutableStateOf(false)
    }

    if (showTimeAlert) {
        TimeAlert(
            modifier = modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(10.dp),
            onAddTimeButtonClick = {
                viewModel.updateTimeStamps(it)
            },
            onDismissRequest = {
                showTimeAlert = false
            }
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
            if (task.startTaskTime != null) {
                Text(text = task.startTaskTime)
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
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    onClick = { viewModel.deleteTaskFirebase() }
                ) {
                    Text(text = "Delete")
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
    onAddTimeButtonClick: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val timeStartState = rememberTimePickerState()

    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                modifier = modifier,
                textAlign = TextAlign.Center,
                text = "Task Time",
                style = MaterialTheme.typography.headlineSmall
            )

            TimeInput(state = timeStartState)

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    onClick = {
                        val formatStartTime = formatTime(timeStartState)

                        onAddTimeButtonClick(formatStartTime)
                        onDismissRequest()
                    }
                ) {
                    Text(text = "Done")
                }
            }
        }
    }
}