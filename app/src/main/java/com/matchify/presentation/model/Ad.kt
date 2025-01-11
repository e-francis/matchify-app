package com.matchify.presentation.model

import androidx.annotation.DrawableRes

data class Ad(
    val text: String,
    @DrawableRes val imageResId: Int
)