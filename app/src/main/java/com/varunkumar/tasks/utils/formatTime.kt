package com.varunkumar.tasks.utils

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
fun formatTimeToTimePickerStateToString(
    timePickerState: TimePickerState
): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
        set(Calendar.MINUTE, timePickerState.minute)
    }
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return format.format(calendar.time)
}

@OptIn(ExperimentalMaterial3Api::class)
fun formatTimePickerStateToLong(
    timePickerState: TimePickerState
): Long {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
        set(Calendar.MINUTE, timePickerState.minute)
    }

    return calendar.timeInMillis
}

fun formatLongToString(time: Long): String {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = time
    }

    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return format.format(calendar.time)
}