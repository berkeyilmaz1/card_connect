package com.berkeyilmaz.cardapp.domain.auth

import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.google.firebase.auth.FirebaseUser


interface AuthRepository {
    suspend fun getCurrentUser(): ResponseState<FirebaseUser?>

    suspend fun reloadCurrentUser(): Boolean

    suspend fun logInWithEmail(email: String, password: String): ResponseState<Unit>

    suspend fun signUpWithEmail(email: String, password: String): ResponseState<Unit>

    suspend fun signInWithGoogle(): ResponseState<Unit>

    suspend fun sendForgotPasswordEmail(email: String): ResponseState<Unit>

    suspend fun sendEmailVerification(): ResponseState<Unit>

    suspend fun logout(): ResponseState<Unit>

    suspend fun deleteAccount(): ResponseState<Unit>
}