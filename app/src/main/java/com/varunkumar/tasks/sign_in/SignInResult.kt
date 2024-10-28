package com.varunkumar.tasks.sign_in

import com.varunkumar.tasks.models.UserData

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)