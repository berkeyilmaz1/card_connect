package com.berkeyilmaz.cardapp.presentation.chat.viewmodel

import com.berkeyilmaz.cardapp.domain.chat.model.ChatMessage
import com.berkeyilmaz.cardapp.domain.chat.model.ChatState
import com.berkeyilmaz.cardapp.domain.contact.model.Contact

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val contacts: List<Contact> = emptyList(),
    val chatState: ChatState = ChatState.Idle
)