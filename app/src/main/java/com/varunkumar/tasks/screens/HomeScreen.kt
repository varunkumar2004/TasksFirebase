package com.varunkumar.tasks.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.varunkumar.tasks.TaskCategoriesView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
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
            tasks = listOf("Task 1", "Task 2", "Task 3")
        )
    }
}