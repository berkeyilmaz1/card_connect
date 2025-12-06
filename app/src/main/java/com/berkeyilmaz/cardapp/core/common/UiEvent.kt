package com.berkeyilmaz.cardapp.core.common

import androidx.annotation.StringRes

sealed class UiEvent {
    data class ShowToast(
        @field:StringRes val message: Int
    ) : UiEvent()

    data class ShowSnackBar(
        val message: String, val actionLabel: String? = null
    ) : UiEvent()

    data class Navigate(
        val route: String
    ) : UiEvent()

    data object NavigateBack : UiEvent()
}