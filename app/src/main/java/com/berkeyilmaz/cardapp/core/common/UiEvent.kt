package com.berkeyilmaz.cardapp.core.common

import androidx.annotation.StringRes

sealed class UiEvent {
    data class ShowToast(
        @field:StringRes val message: Int
    ) : UiEvent()

    data class ShowSnackBar(
        @field:StringRes val message: Int, @field:StringRes val actionLabel: Int? = null
    ) : UiEvent()

    data class Navigate(
        val route: String
    ) : UiEvent()

    data object NavigateBack : UiEvent()
}