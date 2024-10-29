package com.varunkumar.tasks.models

data class Task (
    val title: String = "",
    val description: String = "",
    val creationTime: Long? = null,
    val startTaskTime: Long? = null,
    val endTaskTime: Long? = null,
    val isCompleted: Boolean = false
)