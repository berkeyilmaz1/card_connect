package com.berkeyilmaz.cardapp.domain.chat.model

import com.berkeyilmaz.cardapp.domain.contact.model.Contact

sealed class ChatMessage {
    abstract val id: String
    abstract val timestamp: Long

    data class SystemMessage(
        override val id: String, override val timestamp: Long, val text: Int
    ) : ChatMessage()

    data class UserTextMessage(
        override val id: String, override val timestamp: Long, val text: String
    ) : ChatMessage()

    data class AITextMessage(
        override val id: String, override val timestamp: Long, val text: String
    ) : ChatMessage()

    data class AIContactResult(
        override val id: String,
        override val timestamp: Long,
        val text: Int? = null,
        val contact: Contact
    ) : ChatMessage()

    data class AIMultipleResults(
        override val id: String,
        override val timestamp: Long,
        val text: Int? = null,
        val contacts: List<Contact>
    ) : ChatMessage()

    data class AINoResult(
        override val id: String, override val timestamp: Long
    ) : ChatMessage()
}