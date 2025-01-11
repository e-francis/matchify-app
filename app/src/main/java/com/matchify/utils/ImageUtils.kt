package com.matchify.utils

import android.content.Context
import android.net.Uri
import android.util.Base64

object ImageUtils {
    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                val base64String = Base64.encodeToString(bytes, Base64.NO_WRAP)
                val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"

                "data:$mimeType;base64,$base64String"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}