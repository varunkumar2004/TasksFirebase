package com.varunkumar.tasks.sign_in

import androidx.lifecycle.ViewModel
import com.varunkumar.tasks.models.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(
        result: SignInResult
    ) {
        _state.update {
            it.copy(
                user = result.data,
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage
            )
        }
    }

    fun resetState() {
        _state.update { SignInState() }
    }
}

data class SignInState(
    val user: UserData? = null,
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)