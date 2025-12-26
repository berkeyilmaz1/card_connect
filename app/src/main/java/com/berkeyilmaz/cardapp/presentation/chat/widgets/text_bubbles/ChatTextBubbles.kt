package com.berkeyilmaz.cardapp.presentation.chat.widgets.text_bubbles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.presentation.chat.widgets.animations.ChatItemAnimation

@Composable
fun UserMessageBubble(
    text: String,
    messageId: String? = null
) {
    ChatItemAnimation(key = messageId) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Surface(
                shape = RoundedCornerShape(
                    bottomEnd = 0.dp,
                    bottomStart = dimensionResource(R.dimen.spacer_16),
                    topEnd = dimensionResource(R.dimen.spacer_16),
                    topStart = dimensionResource(R.dimen.spacer_16)
                ), color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_lowNormal)),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun AITextBubble(
    text: String,
    messageId: String? = null
) {
    ChatItemAnimation(
        key = messageId,
        delayMillis = if (messageId != null) 100 else 0
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Surface(
                shape = RoundedCornerShape(
                    bottomEnd = dimensionResource(R.dimen.spacer_16),
                    bottomStart = 0.dp,
                    topEnd = dimensionResource(R.dimen.spacer_16),
                    topStart = dimensionResource(R.dimen.spacer_16)
                ), color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_lowNormal)),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ThinkingBubble(
    text: String? = null,
    messageId: String? = null
) {
    ChatItemAnimation(key = messageId, delayMillis = 0) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Surface(
                shape = RoundedCornerShape(
                    bottomEnd = dimensionResource(R.dimen.spacer_16),
                    bottomStart = 0.dp,
                    topEnd = dimensionResource(R.dimen.spacer_16),
                    topStart = dimensionResource(R.dimen.spacer_16)
                ), color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(modifier = Modifier.padding(dimensionResource(R.dimen.padding_lowNormal)), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    if (text != null) {
                        Text(text = text, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
