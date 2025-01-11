package com.matchify.data.model.response

import com.google.gson.annotations.SerializedName
import com.matchify.data.model.User

data class LoginResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: User?
)
