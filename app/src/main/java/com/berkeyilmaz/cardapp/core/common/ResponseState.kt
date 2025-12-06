package com.berkeyilmaz.cardapp.core.common

sealed class ResponseState<out T> {
    data class Success<out T>(val data: T) : ResponseState<T>()
    data class Error(val exception: Throwable) : ResponseState<Nothing>()
}