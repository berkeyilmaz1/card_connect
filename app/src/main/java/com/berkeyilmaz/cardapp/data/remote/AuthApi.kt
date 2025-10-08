package com.berkeyilmaz.cardapp.data.remote

import com.berkeyilmaz.cardapp.data.model.AuthResponse
import com.berkeyilmaz.cardapp.data.model.LoginRequest
import com.berkeyilmaz.cardapp.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface AuthApi {
    @POST("api/v1/auth/login")
    @Headers("accept: */*", "Content-Type: application/json")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/v1/auth/register")
    @Headers("accept: */*", "Content-Type: application/json")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET
    suspend fun refresh(@Url refreshUrl: String): Response<AuthResponse>
}