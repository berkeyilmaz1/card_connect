package com.berkeyilmaz.cardapp.presentation.main.home.models

data class HomeNotification(
    val id: NotificationID,
    val title: String,
    val subtitle: String,
    val buttonText: String,
    val onButtonClick: () -> Unit
)

enum class NotificationID {
    EMAIL_VERIFICATION, PROFILE_INCOMPLETE
}
