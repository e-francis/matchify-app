package com.matchify.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPasscode(passcode: String): Boolean {
        return passcode.length == Constants.PASSCODE_LENGTH && passcode.all { it.isDigit() }
    }

    fun calculateAge(dateOfBirth: Calendar): Int {
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dateOfBirth.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }

    fun validateCreateProfileInput(
        firstName: String,
        lastName: String,
        email: String,
        passcode: String,
        confirmPasscode: String,
        dob: String,
        location: String,
        interests: List<String>,
        profilePicture: String?
    ): ValidationResult {
        return when {
            firstName.isBlank() -> ValidationResult.Error("First name is required")
            lastName.isBlank() -> ValidationResult.Error("Last name is required")
            !isValidEmail(email) -> ValidationResult.Error("Invalid email address")
            !isValidPasscode(passcode) -> ValidationResult.Error("Passcode must be 6 digits")
            passcode != confirmPasscode -> ValidationResult.Error("Passcodes do not match")
            dob.isBlank() -> ValidationResult.Error("Date of birth is required")
            location.isBlank() -> ValidationResult.Error("Location is required")
            interests.isEmpty() -> ValidationResult.Error("Select at least one interest")
            interests.size > Constants.MAX_INTERESTS -> ValidationResult.Error("Maximum ${Constants.MAX_INTERESTS} interests allowed")
            profilePicture == null -> ValidationResult.Error("Profile picture is required")
            else -> ValidationResult.Success
        }
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

// PermissionUtils.kt
object PermissionUtils {
    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasGalleryPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestCameraPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.CAMERA),
            Constants.CAMERA_PERMISSION_REQUEST
        )
    }

    fun requestGalleryPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                Constants.GALLERY_PERMISSION_REQUEST
            )
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.GALLERY_PERMISSION_REQUEST
            )
        }
    }
}