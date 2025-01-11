package com.matchify.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val firstName: String,
    val lastName: String,
    val dob: String,
    val location: String,
    val interests: List<String>,
    val sex: String,
    val email: String,
    val passcode: Int? = null,
    val profilePicture: String
) : Parcelable