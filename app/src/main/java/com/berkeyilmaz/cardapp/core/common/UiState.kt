package com.berkeyilmaz.cardapp.core.common

import androidx.annotation.StringRes

/**
 * Generic UI state sealed class to represent Loading, Success, and Error states.
 *
 * @param T The type of data held in the Success state.
 */
sealed class UiState<out T : Any> {

    data object Loading : UiState<Nothing>()

    data class Success<out T : Any>(val data: T) : UiState<T>()

    data class Error(
        @param:StringRes val errorMessage: Int,
    ) : UiState<Nothing>()
}