package com.berkeyilmaz.cardapp.data.remote

import com.berkeyilmaz.cardapp.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.HttpException

class TokenInterceptor(
    private val tokenManager: TokenManager,
    private val api: AuthApi
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Token eklenmemesi gereken endpoint'leri kontrol et
        val excludedPaths = listOf("/api/v1/auth/login", "/api/v1/auth/register")
        if (excludedPaths.any { originalRequest.url.encodedPath.contains(it) }) {
            return chain.proceed(originalRequest)
        }

        val requestBuilder = originalRequest.newBuilder()

        // Access token ekle
        val accessToken = runBlocking { tokenManager.getAccessToken() }
        accessToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val response = chain.proceed(requestBuilder.build())

        // Eğer 401 Unauthorized dönerse refresh dene
        if (response.code == 401) {
            response.close()
            val refreshToken = runBlocking { tokenManager.getRefreshToken() }

            if (!refreshToken.isNullOrEmpty()) {
                try {
                    val newTokens = runBlocking {
                        api.refresh("http://192.168.1.103:8080/api/v1/auth/refresh?token=$refreshToken")
                    }
                    if (!newTokens.isSuccessful) {
                        runBlocking { tokenManager.clearTokens() }
                        return response
                    }
                    val newTokensBody = newTokens.body() ?: run {
                        runBlocking { tokenManager.clearTokens() }
                        return response
                    }
                    runBlocking {
                        tokenManager.saveTokens(
                            newTokensBody.accessToken,
                            newTokensBody.refreshToken
                        )
                    }

                    // Yeni token ile isteği tekrarla
                    val newRequest = originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer ${newTokensBody.accessToken}")
                        .build()
                    return chain.proceed(newRequest)
                } catch (e: HttpException) {
                    runBlocking { tokenManager.clearTokens() }
                }
            }
        }

        return response
    }

}
