package com.berkeyilmaz.cardapp.data.model

import android.content.Context
import com.berkeyilmaz.cardapp.R
import com.google.gson.Gson
import retrofit2.Response

data class ApiError(
    val error: String, val message: String
)

fun <T> Response<T>.getErrorMessage(context: Context): String {
    return try {
        val errorBody = this.errorBody()?.string()
        val apiError = Gson().fromJson(errorBody, ApiError::class.java)
        apiError?.message ?: context.getString(R.string.unknown_error)
    } catch (e: Exception) {
        context.getString(R.string.unknown_error)
    }
}
