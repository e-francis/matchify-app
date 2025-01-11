package com.matchify.data.repository

import com.matchify.data.api.MatchifyApi
import com.matchify.data.model.User
import com.matchify.data.model.request.LoginRequest
import com.matchify.data.model.response.ApiErrorResponse
import com.matchify.data.model.response.CreateProfileResponse
import com.matchify.data.model.response.LoginResponse
import com.matchify.utils.NetworkResult
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    api: MatchifyApi,
    private val moshi: Moshi
) : UserRepository(api) {

    override suspend fun login(email: String, passcode: String): Result<LoginResponse> {
        return try {
            val loginRequest = LoginRequest(email, passcode)
            val networkResult = safeApiCall { super.api.login(loginRequest) }

            when (networkResult) {
                is NetworkResult.Success -> Result.success(networkResult.data)
                is NetworkResult.Error -> Result.failure(Exception(networkResult.message))
                is NetworkResult.Loading -> Result.failure(Exception("Unexpected loading state"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createProfile(user: User): Result<CreateProfileResponse> {
        return try {
            val networkResult = safeApiCall { super.api.createProfile(user) }

            when (networkResult) {
                is NetworkResult.Success -> Result.success(networkResult.data)
                is NetworkResult.Error -> Result.failure(Exception(networkResult.message))
                is NetworkResult.Loading -> Result.failure(Exception("Unexpected loading state"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Private helper method for safe API calls
    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                handleResponse(response)
            } catch (e: Exception) {
                when (e) {
                    is IOException -> NetworkResult.Error("Network Error: Check your connection")
                    else -> NetworkResult.Error("Unexpected error: ${e.localizedMessage}")
                }
            }
        }
    }

    // Parse response and handle errors
    private fun <T> handleResponse(response: Response<T>): NetworkResult<T> {
        return if (response.isSuccessful) {
            response.body()?.let {
                NetworkResult.Success(it)
            } ?: NetworkResult.Error("Empty response", response.code())
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = parseErrorBody(errorBody)

            NetworkResult.Error(
                message = errorMessage,
                code = response.code(),
                errorBody = errorBody
            )
        }
    }

    // Parse error body using Moshi
    private fun parseErrorBody(errorBody: String?): String {
        return try {
            val jsonAdapter = moshi.adapter(ApiErrorResponse::class.java)
            val parsedError = jsonAdapter.fromJson(errorBody ?: "{}")
            parsedError?.message
                ?: parsedError?.error
                ?: "An unknown error occurred"
        } catch (e: Exception) {
            "Failed to parse error message"
        }
    }
}