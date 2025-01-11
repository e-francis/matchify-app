package com.matchify.presentation.auth.login

import com.matchify.data.model.response.LoginResponse

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    data class Success(
        val user: Result<LoginResponse>,
        val message: String
    ) : LoginState()
    data class Error(val message: String) : LoginState()
}