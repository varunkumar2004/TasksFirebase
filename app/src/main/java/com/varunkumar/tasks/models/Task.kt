package com.varunkumar.tasks.models

data class Task (
    val title: String = "",
    val description: String = "",
    val creationTime: Long? = null,
    val startTaskTime: String? = null,
    val endTaskTime: String? = null,
    val imageUri: String? = null,
    val taskCategory: String? = null,
    val isCompleted: Boolean = false
)