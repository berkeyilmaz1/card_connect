package com.berkeyilmaz.cardapp.data.model

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String
)