package com.berkeyilmaz.cardapp.domain.chat.model

sealed class ChatState {
    data object Idle : ChatState()
    data object UserTyping : ChatState()
    data object AIThinking : ChatState()

    data object ResultFound : ChatState()
    data object MultipleResults : ChatState()
    data object NoResult : ChatState()

    data class Error(val message: String) : ChatState()
}
