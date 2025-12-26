package com.berkeyilmaz.cardapp.presentation.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.berkeyilmaz.cardapp.domain.chat.model.ChatState
import com.berkeyilmaz.cardapp.presentation.chat.viewmodel.ChatViewModel
import com.berkeyilmaz.cardapp.presentation.chat.widgets.MessageInputBar
import com.berkeyilmaz.cardapp.presentation.chat.widgets.MessageList

@Composable
fun ChatView(
    chatViewModel: ChatViewModel,
) {
    val uiState by chatViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        chatViewModel.fetchContacts()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
    ) {

        MessageList(
            messages = uiState.messages,
            listState = listState,
            modifier = Modifier.weight(1f),
            isThinking = uiState.chatState is ChatState.AIThinking
        )

        HorizontalDivider()

        MessageInputBar(
            enabled = uiState.chatState !is ChatState.AIThinking,
            onSend = chatViewModel::sendMessage,
        )
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(
                index = uiState.messages.lastIndex
            )
        }
    }
}
