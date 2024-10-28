package com.varunkumar.tasks.models

data class Task (
    val title: String = "",
    val description: String = "",
    val datetime: Long? = null,
    val owner: String = "",
    val isCompleted: Boolean = false
)