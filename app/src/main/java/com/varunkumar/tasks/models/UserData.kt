package com.varunkumar.tasks.models

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val userId: String = "",
    val username: String? = null,
    val profilePictureUrl: String? = null
)