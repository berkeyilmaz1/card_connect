package com.berkeyilmaz.cardapp.domain.auth

import com.google.firebase.auth.FirebaseUser

sealed class AuthResult<out T> {
    data class Success<out T>(val data: T, val message: String? = null) : AuthResult<T>()

    sealed class Error(val message: String) : AuthResult<Nothing>() {
        class Generic(message: String) : Error(message)
        class ReAuthNeeded(message: String) : Error(message)
    }
}

interface AuthRepository {
    suspend fun getCurrentUser(): AuthResult<FirebaseUser?>

    suspend fun reloadCurrentUser(): Boolean

    suspend fun loginOrRegister(email: String, password: String): AuthResult<Unit>

    suspend fun signInWithGoogle(): AuthResult<Unit>

    suspend fun sendForgotPasswordEmail(email: String): AuthResult<Unit>

    suspend fun sendEmailVerification(): AuthResult<Unit>

    suspend fun logout(): AuthResult<Unit>

    suspend fun deleteAccount(): AuthResult<Unit>
}