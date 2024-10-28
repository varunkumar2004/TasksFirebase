package com.varunkumar.tasks.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier
) {
    val datePickerState = rememberDatePickerState()

    Column(
        modifier = modifier
    ) {
        DatePicker(
            title = {

            },
            state = datePickerState
        )
    }
}