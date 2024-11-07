package com.varunkumar.tasks.models

data class Task (
    val title: String = "",
    val description: String? = null,
    val creationTime: Long = 0L,
    val scheduledTime: Long? = null,
    val category: TaskCategory? = null,
    val status: Boolean = false
)