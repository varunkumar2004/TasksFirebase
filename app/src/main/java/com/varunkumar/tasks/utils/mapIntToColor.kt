package com.varunkumar.tasks.utils

import androidx.compose.ui.graphics.Color
import com.varunkumar.tasks.models.TaskCategoryColor

fun mapIntToColor(colorInt: Int): Color {
    return TaskCategoryColor.COLORS[colorInt]
}