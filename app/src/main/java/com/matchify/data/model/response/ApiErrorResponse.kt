package com.matchify.data.model.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiErrorResponse(
    val error: String? = null,
    val message: String? = null,
    val details: List<String>? = null
)