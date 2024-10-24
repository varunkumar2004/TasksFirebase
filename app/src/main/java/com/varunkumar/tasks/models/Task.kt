package com.varunkumar.tasks.models

data class Task (
    val title: String,
    val description: String?,
    val datetime: Long,
    val owner: String
)