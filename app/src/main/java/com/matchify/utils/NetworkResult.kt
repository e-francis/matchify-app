package com.matchify.utils

sealed class NetworkResult<out T> {
    data class Success<T>(
        val data: T,
        val message: String? = null,
        val fullResponse: Any? = null
    ) : NetworkResult<T>()

    data class Error(
        val message: String,
        val code: Int? = null,
        val errorBody: String? = null,
        val fullErrorResponse: Any? = null
    ) : NetworkResult<Nothing>()

    object Loading : NetworkResult<Nothing>()
}