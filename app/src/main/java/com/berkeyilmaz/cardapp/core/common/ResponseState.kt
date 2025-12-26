package com.berkeyilmaz.cardapp.core.common

sealed class ResponseState<out T> {
    data class Success<out T>(val data: T, val message: String? = null) : ResponseState<T>()
    data class Error(val message: String) : ResponseState<Nothing>()
}