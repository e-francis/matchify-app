package com.matchify.data.model

data class Location(
    val city: String,
    val country: String
) {
    override fun toString(): String = "$city, $country"
}