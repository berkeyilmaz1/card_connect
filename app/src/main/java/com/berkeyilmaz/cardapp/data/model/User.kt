package com.berkeyilmaz.cardapp.data.model

data class User(
    val id: String = "",
    val displayName: String? = null,
    val email: String? = null,
    val profilePictureUrl: String? = null
)