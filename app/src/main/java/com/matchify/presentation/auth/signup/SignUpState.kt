package com.matchify.presentation.auth.signup

sealed class SignUpState {
    data object Initial : SignUpState()
    data object Loading : SignUpState()
    data class Success(
        val message: String,
        val profileId: String? = null
    ) : SignUpState()
    data class Error(
        val message: String,
        val code: Int? = null,
        val errorBody: String? = null
    ) : SignUpState()
}