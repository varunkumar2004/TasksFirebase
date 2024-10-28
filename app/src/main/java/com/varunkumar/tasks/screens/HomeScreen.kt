package com.varunkumar.tasks.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.varunkumar.tasks.models.Task
import com.varunkumar.tasks.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onTaskClick: (Task) -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val options = listOf("All", "Completed")
        var selectedIndex by remember { mutableIntStateOf(0) }

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
        ) {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    modifier = Modifier,
                    shape = RectangleShape,
                    onClick = { selectedIndex = index },
                    selected = index == selectedIndex
                ) {
                    Text(label)
                }
            }
        }

        TaskCategoriesView(
            modifier = Modifier.fillMaxWidth(),
            tasks = viewModel.tasks,
            onTaskClick = onTaskClick
        )
    }
}

@Composable
private fun TaskCategoriesView(
    modifier: Modifier = Modifier,
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
    ) {
        itemsIndexed(tasks) { index, item ->
            TaskItem(
                modifier = modifier
                    .clip(RoundedCornerShape(5.dp))
                    .clickable { onTaskClick(item) }
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 10.dp, horizontal = 5.dp),
                task = item
            )

            if (index != tasks.lastIndex) Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
private fun TaskItem(
    modifier: Modifier = Modifier,
    task: Task
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            modifier = Modifier,
            selected = false,
            onClick = { /*TODO*/ }
        )

        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = task.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}