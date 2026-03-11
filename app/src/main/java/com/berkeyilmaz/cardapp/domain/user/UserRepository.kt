package com.berkeyilmaz.cardapp.domain.user

import com.berkeyilmaz.cardapp.core.common.ResponseState

interface UserRepository {
    suspend fun saveAnalyticsConsent(userId: String, consent: Boolean): ResponseState<Unit>
    suspend fun getAnalyticsConsent(userId: String): ResponseState<Boolean>
}
