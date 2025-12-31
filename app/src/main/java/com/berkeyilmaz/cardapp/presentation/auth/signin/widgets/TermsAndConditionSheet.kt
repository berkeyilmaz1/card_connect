package com.berkeyilmaz.cardapp.presentation.auth.signin.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.CustomAppButton
import kotlinx.coroutines.delay

@Composable
fun TermsAndConditionSheet(
    onDeclineClick: () -> Unit,
    onAcceptClick: () -> Unit,
    loadingState: Boolean = false,
) {
    val listState = rememberLazyListState()
    var hasScrolledToBottom by remember { mutableStateOf(false) }
    var timerCompleted by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableIntStateOf(10) }

    val isAtBottom by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - 1 &&
                    lastVisibleItem.offset + lastVisibleItem.size <= listState.layoutInfo.viewportEndOffset
        }
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom && !hasScrolledToBottom) {
            hasScrolledToBottom = true
        }
    }

    LaunchedEffect(Unit) {
        repeat(10) {
            delay(1000L)
            remainingSeconds -= 1
        }
        timerCompleted = true
    }

    val canAccept = hasScrolledToBottom && timerCompleted

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding(),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_lowNormal)),
        ) {
            item {
                Text(
                    text = stringResource(R.string.eula_text),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_lowNormal)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        ) {
            CustomAppButton(
                text = stringResource(R.string.decline),
                onClick = onDeclineClick,
                fullWidth = false,
                modifier = Modifier.weight(1f)
            )

            CustomAppButton(
                text = if (!timerCompleted) {
                    "${stringResource(R.string.accept)} (${remainingSeconds}s)"
                } else {
                    stringResource(R.string.accept)
                },
                onClick = onAcceptClick,
                loading = loadingState,
                enabled = canAccept,
                fullWidth = false,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

