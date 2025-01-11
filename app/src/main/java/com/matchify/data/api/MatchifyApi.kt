package com.matchify.data.api

import com.matchify.data.model.User
import com.matchify.data.model.request.LoginRequest
import com.matchify.data.model.response.CreateProfileResponse
import com.matchify.data.model.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MatchifyApi {
    @POST("v1/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("v1/api/create-profile")
    suspend fun createProfile(@Body user: User): Response<CreateProfileResponse>
}