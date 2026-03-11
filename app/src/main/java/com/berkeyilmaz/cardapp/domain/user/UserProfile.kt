package com.berkeyilmaz.cardapp.domain.user

data class UserProfile(
    val analyticsConsent: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
