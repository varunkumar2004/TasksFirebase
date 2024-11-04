package com.varunkumar.tasks.models

data class Task (
    val title: String = "",
    val description: String? = null,
    val creationTime: Long = 0L,
    val startTaskTime: String? = null,
    val imageUri: String? = null,
    val taskCategory: TaskCategory? = null,
    val completed: Boolean = false
)