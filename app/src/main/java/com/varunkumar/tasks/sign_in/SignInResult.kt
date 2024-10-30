package com.varunkumar.tasks.sign_in

import com.varunkumar.tasks.models.UserData

data class SignInResult(
    val data: UserData? = null,
    val errorMessage: String? = null
)