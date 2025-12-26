package com.berkeyilmaz.cardapp.presentation.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.domain.chat.model.ChatMessage
import com.berkeyilmaz.cardapp.domain.chat.model.ChatState
import com.berkeyilmaz.cardapp.domain.chat.usecase.AIContactResult
import com.berkeyilmaz.cardapp.domain.chat.usecase.FindContactByMessageUseCase
import com.berkeyilmaz.cardapp.domain.contact.usecase.GetRemoteContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val findContactByMessageUseCase: FindContactByMessageUseCase,
    private val getUserContacts: GetRemoteContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    init {
        sendWelcomeMessage()
    }


    fun fetchContacts() = viewModelScope.launch {
        val contacts = getUserContacts()
        _uiState.update { it.copy(contacts = contacts.getOrDefault(emptyList())) }
    }

    private fun sendWelcomeMessage() {
        val welcomeMessage = ChatMessage.SystemMessage(
            id = "0",
            timestamp = System.currentTimeMillis(),
            text = R.string.welcome_how_can_i_assist_you_in_finding_contacts
        )
        _uiState.update { it.copy(messages = it.messages + welcomeMessage) }
    }

    fun sendMessage(message: String) {
        val userMessage = ChatMessage.UserTextMessage(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            text = message
        )

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage, chatState = ChatState.AIThinking
            )
        }

        viewModelScope.launch {
            handleAIResponse(message)
        }
    }


    suspend fun handleAIResponse(message: String) {
        val contacts = _uiState.value.contacts
        if (contacts.isEmpty()) {
            fetchContacts()
            val noContactsMessage = ChatMessage.SystemMessage(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                text = R.string.no_contacts_available_to_search
            )
            _uiState.update {
                it.copy(
                    messages = it.messages + noContactsMessage, chatState = ChatState.Idle
                )
            }
            return
        }
        when (val result = findContactByMessageUseCase(message, contacts)) {
            is AIContactResult.Single -> {
                addMessage(
                    ChatMessage.AIContactResult(
                        id = UUID.randomUUID().toString(),
                        timestamp = System.currentTimeMillis(),
                        contact = result.contact,
//                        text =
                    ), ChatState.ResultFound
                )
            }

            is AIContactResult.Multiple -> {
                addMessage(
                    ChatMessage.AIMultipleResults(
                        id = UUID.randomUUID().toString(),
                        timestamp = System.currentTimeMillis(),
                        contacts = result.contacts
                    ), ChatState.MultipleResults
                )
            }

            is AIContactResult.NoResult -> {
                addMessage(
                    ChatMessage.AINoResult(
                        id = UUID.randomUUID().toString(), timestamp = System.currentTimeMillis()
                    ), ChatState.NoResult
                )
            }

        }
    }

    private fun addMessage(message: ChatMessage, state: ChatState) {
        _uiState.update {
            it.copy(
                messages = it.messages + message, chatState = state
            )
        }
    }
}