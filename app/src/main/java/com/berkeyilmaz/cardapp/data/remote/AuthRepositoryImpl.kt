package com.berkeyilmaz.cardapp.data.remote


import com.berkeyilmaz.cardapp.data.model.AuthResponse
import com.berkeyilmaz.cardapp.data.model.LoginRequest
import com.berkeyilmaz.cardapp.data.model.RegisterRequest
import com.berkeyilmaz.cardapp.data.model.getErrorMessage
import com.berkeyilmaz.cardapp.domain.AuthRepository
import com.berkeyilmaz.cardapp.domain.AuthResult
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
//    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(loginRequest: LoginRequest): AuthResult<AuthResponse> {
        return try {
            val response = api.login(loginRequest)

            if (!response.isSuccessful) {
                return AuthResult.Error(response.getErrorMessage())
            }

            val body = response.body() ?: return AuthResult.Error("Empty response")
            AuthResult.Success(body)

        } catch (e: IOException) {
            AuthResult.Error("Network error: ${e.localizedMessage}")
        } catch (e: Exception) {
            AuthResult.Error("Unexpected error: ${e.localizedMessage}")
        }
    }

    override suspend fun register(registerRequest: RegisterRequest): AuthResult<AuthResponse> {
        return try {
            val response = api.register(registerRequest)

            if (!response.isSuccessful) {
                return AuthResult.Error(response.getErrorMessage())
            }

            val body = response.body() ?: return AuthResult.Error("Empty response")
            AuthResult.Success(body)

        } catch (e: IOException) {
            AuthResult.Error("Network error: ${e.localizedMessage}")
        } catch (e: Exception) {
            AuthResult.Error("Unexpected error: ${e.localizedMessage}")
        }
    }

//
//    override suspend fun refresh(): AuthResult<AuthResponse> {
//        return try {
//            val refreshToken = tokenManager.getRefreshToken()
//                ?: return AuthResult.Error("No refresh token found")
//
//            val response = api.refresh("url")
//            if (response.accessToken.isNotEmpty() && response.refreshToken.isNotEmpty()) {
//                tokenManager.saveTokens(response.accessToken, response.refreshToken)
//                AuthResult.Success(response)
//            } else {
//                AuthResult.Error("Invalid refresh response")
//            }
//        } catch (e: IOException) {
//            AuthResult.Error("Network error: ${e.localizedMessage}")
//        } catch (e: Exception) {
//            AuthResult.Error("Unexpected error: ${e.localizedMessage}")
//        }
//    }

    override suspend fun logout(): AuthResult<Unit> {
        return try {
//            tokenManager.clearTokens()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error("Failed to logout: ${e.localizedMessage}")
        }
    }
}