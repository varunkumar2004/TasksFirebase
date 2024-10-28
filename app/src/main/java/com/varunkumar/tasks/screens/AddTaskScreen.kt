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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.MoreTime
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.varunkumar.tasks.models.Task
import com.varunkumar.tasks.viewmodels.HomeViewModel

@Composable
fun AddTaskScreen(
    modifier: Modifier = Modifier,
    task: Task,
    viewModel: HomeViewModel,
    onDismissRequest: () -> Unit
) {
    val fModifier = Modifier.fillMaxWidth()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            modifier = fModifier,
            textAlign = TextAlign.Center,
            text = "Add Task",
            style = MaterialTheme.typography.headlineSmall
        )

        TaskTitle(
            modifier = fModifier,
            task = task,
            onValueTitleChange = viewModel::updateTitle,
            onValueDescriptionChange = viewModel::updateDescription
        )

        TaskButtons(
            modifier = fModifier,
            task = task,
            onDismissRequest = onDismissRequest
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
            value = task.description,
            colors = textFieldColors,
            shape = RoundedCornerShape(5.dp),
            label = { Text(text = "Description") },
            onValueChange = onValueDescriptionChange
        )
    }
}

@Composable
private fun TaskButtons(
    modifier: Modifier = Modifier,
    task: Task,
    onDismissRequest: () -> Unit
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    var showTimeAlert by remember {
        mutableStateOf(false)
    }

    if (showTimeAlert) {
        TimeAlert(
            modifier = modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(10.dp),
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
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    launcher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            ) {
                Icon(
                    imageVector = if (selectedImageUri != null && selectedImageUri != Uri.EMPTY)
                        Icons.Outlined.Photo
                    else Icons.Outlined.AddPhotoAlternate,
                    contentDescription = "Add Image"
                )
            }

            IconButton(
                onClick = { showTimeAlert = true }
            ) {
                Icon(imageVector = Icons.Outlined.MoreTime, contentDescription = "Add Time")
            }

            Text(text = task.datetime.toString())
        }

        Button(onClick = onDismissRequest) {
            Text(text = "Add")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeAlert(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit
) {
    val timeInputState = rememberTimePickerState()

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

            Column {
                Text(
                    text = "Starting Time",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.heightIn(2.dp))
                TimeInput(state = timeInputState)
            }

            Column {
                Text(
                    text = "Ending Time",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.heightIn(2.dp))
                TimeInput(state = timeInputState)
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(onClick = onDismissRequest) {
                    Text(text = "Done")
                }
            }
        }
    }
}