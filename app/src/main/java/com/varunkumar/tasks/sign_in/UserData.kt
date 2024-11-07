package com.varunkumar.tasks.sign_in

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val userId: String = "",
    val username: String? = null,
    val profilePictureUrl: String? = null
)