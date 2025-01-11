package com.matchify.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast


fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Uri.toBitmap(context: Context): Bitmap? {
    return try {
        context.contentResolver.openInputStream(this)?.use {
            BitmapFactory.decodeStream(it)
        }
    } catch (e: Exception) {
        null
    }
}

fun Bitmap.compress(maxSizeBytes: Int): Bitmap? {
    var quality = 100
    var byteCount: Int

    do {
        val outputStream = java.io.ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        byteCount = outputStream.toByteArray().size
        quality -= 10
    } while (byteCount > maxSizeBytes && quality > 0)

    return if (quality > 0) this else null
}