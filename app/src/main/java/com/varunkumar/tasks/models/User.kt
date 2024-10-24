package com.varunkumar.tasks.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val tasks: List<Task> = emptyList()
)