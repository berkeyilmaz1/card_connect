package com.berkeyilmaz.cardapp.data.model

import com.google.gson.Gson
import retrofit2.Response

data class ApiError(
    val error: String, val message: String
)

fun <T> Response<T>.getErrorMessage(): String {
    return try {
        val errorBody = this.errorBody()?.string()
        val apiError = Gson().fromJson(errorBody, ApiError::class.java)
        apiError?.message ?: "Unknown error"
    } catch (e: Exception) {
        "Unknown error"
    }
}
