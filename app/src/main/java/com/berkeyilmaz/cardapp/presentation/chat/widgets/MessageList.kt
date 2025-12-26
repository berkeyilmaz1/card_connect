package com.berkeyilmaz.cardapp.presentation.chat.widgets

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.core.net.toUri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.domain.chat.model.ChatMessage
import com.berkeyilmaz.cardapp.presentation.chat.widgets.cards.MultipleContactsResultCard
import com.berkeyilmaz.cardapp.presentation.chat.widgets.cards.SingleContactResultCard
import com.berkeyilmaz.cardapp.presentation.chat.widgets.text_bubbles.AITextBubble
import com.berkeyilmaz.cardapp.presentation.chat.widgets.text_bubbles.ThinkingBubble
import com.berkeyilmaz.cardapp.presentation.chat.widgets.text_bubbles.UserMessageBubble

@Composable
fun MessageList(
    messages: List<ChatMessage>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    isThinking: Boolean = false
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(dimensionResource(R.dimen.padding_lowNormal)),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.padding_small)
        )
    ) {
        items(
            items = messages, key = { it.id }) { message ->
            MessageItem(message)
        }

        // if AI is thinking, show a left-aligned thinking bubble at the end of the list
        if (isThinking) {
            item {
                ThinkingBubble(
                    text = stringResource(R.string.thinking)
                )
            }
        }
    }
}


@Composable
fun MessageItem(message: ChatMessage) {
    val context = LocalContext.current

    val onCall: (String) -> Unit = { phoneNumber ->
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
        }
        context.startActivity(intent)
    }

    val onEmail: (String) -> Unit = { email ->
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:$email".toUri()
        }
        context.startActivity(intent)
    }

    when (message) {
        is ChatMessage.UserTextMessage -> {
            UserMessageBubble(message.text)
        }

        is ChatMessage.AITextMessage -> {
            AITextBubble(message.text)
        }

        is ChatMessage.AIContactResult -> {
            SingleContactResultCard(
                contact = message.contact,
                onCall = onCall,
                onEmail = onEmail
            )
        }

        is ChatMessage.AIMultipleResults -> {
            MultipleContactsResultCard(
                contacts = message.contacts,
                onCall = onCall,
                onEmail = onEmail
            )
        }

        is ChatMessage.AINoResult -> {
            AITextBubble(stringResource(R.string.ai_chat_no_result))
        }

        is ChatMessage.SystemMessage -> AITextBubble(stringResource(message.text))
    }
}
