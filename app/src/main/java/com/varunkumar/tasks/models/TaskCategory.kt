package com.varunkumar.tasks.models

import kotlinx.serialization.Serializable

@Serializable
data class TaskCategory(
    val category: String? = null
)