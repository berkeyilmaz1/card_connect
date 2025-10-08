package com.berkeyilmaz.cardapp.domain

import com.berkeyilmaz.cardapp.data.model.AuthResponse
import com.berkeyilmaz.cardapp.data.model.LoginRequest
import com.berkeyilmaz.cardapp.data.model.RegisterRequest

sealed class AuthResult<out T> {
    data class Success<out T>(val data: T) : AuthResult<T>()
    data class Error(val exception: String) : AuthResult<Nothing>()
}

interface AuthRepository {
    suspend fun register(registerRequest: RegisterRequest): AuthResult<AuthResponse>
    suspend fun login(loginRequest: LoginRequest): AuthResult<AuthResponse>
//    suspend fun refresh(): AuthResult<AuthResponse>
    suspend fun logout(): AuthResult<Unit>
}