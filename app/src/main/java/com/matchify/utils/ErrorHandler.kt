package com.matchify.utils

import retrofit2.HttpException
import java.io.IOException

object ErrorHandler {
    fun handleError(throwable: Throwable): String {
        return when (throwable) {
            is IOException -> "Network error. Please check your connection."
            is HttpException -> handleHttpError(throwable)
            else -> "An unexpected error occurred."
        }
    }

    private fun handleHttpError(error: HttpException): String {
        return when (error.code()) {
            401 -> "Invalid credentials"
            403 -> "Access denied"
            404 -> "Resource not found"
            else -> "Server error: ${error.code()}"
        }
    }
}