package com.matchify.data.model.response

import com.google.gson.annotations.SerializedName

data class CreateProfileResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("profileId") val profileId: String? = null
)
