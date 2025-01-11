package com.matchify.data.repository

import com.matchify.data.api.MatchifyApi
import com.matchify.data.model.User
import com.matchify.data.model.request.LoginRequest
import com.matchify.data.model.response.CreateProfileResponse
import com.matchify.data.model.response.LoginResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class UserRepository @Inject constructor(
    val api: MatchifyApi
) {
    open suspend fun login(email: String, passcode: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(email, passcode))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    open suspend fun createProfile(user: User): Result<CreateProfileResponse> {
        return try {
            val response = api.createProfile(user)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Profile creation failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

