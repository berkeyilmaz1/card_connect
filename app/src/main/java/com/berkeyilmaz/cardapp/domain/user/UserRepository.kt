package com.berkeyilmaz.cardapp.domain.user

import com.berkeyilmaz.cardapp.core.common.ResponseState

interface UserRepository {
    suspend fun saveAnalyticsConsent(userId: String, consent: Boolean): ResponseState<Unit>
    suspend fun getAnalyticsConsent(userId: String): ResponseState<Boolean>
    suspend fun updateDisplayName(displayName: String): ResponseState<Unit>
    suspend fun savePhotoUrl(userId: String, photoUrl: String): ResponseState<Unit>
    suspend fun getPhotoUrl(userId: String): ResponseState<String?>
    suspend fun saveUserInfo(userId: String, displayName: String, phone: String): ResponseState<Unit>
    suspend fun getUserInfo(userId: String): ResponseState<UserInfo>
    suspend fun deleteUserData(userId: String): ResponseState<Unit>
    suspend fun initUserData(userId: String): ResponseState<Unit>
    suspend fun setInitSync(value: Boolean): ResponseState<Unit>
}

data class UserInfo(
    val displayName: String = "",
    val phone: String = ""
)
