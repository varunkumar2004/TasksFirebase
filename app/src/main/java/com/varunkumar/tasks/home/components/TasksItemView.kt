package com.varunkumar.tasks.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TaskItemView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All",
                style = MaterialTheme.typography.bodyLarge
            )

            Icon(
                imageVector = Icons.Default.FilterAlt,
                contentDescription = null
            )
        }

        LazyColumn(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
        ) {
            items(5) {
                ListItem(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp)),
                    headlineContent = {
                        Text(text = "Task $it")
                    },
                    supportingContent = {
                        Text(text = "description for task $it")
                    },
                    leadingContent = {
                        RadioButton(
                            modifier = Modifier
                                .size(20.dp),
                            selected = false,
                            onClick = {}
                        )
                    }
                )

                if (it != 4) Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskViewPrev() {
    TaskItemView(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
    )
}