package com.berkeyilmaz.cardapp.data.model

data class LoginRequest(
    val identifier: String,
    val password: String,
)