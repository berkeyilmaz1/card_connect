package com.berkeyilmaz.cardapp.presentation.chat.widgets.animations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun ChatItemAnimation(
    modifier: Modifier = Modifier,
    key: Any? = null,
    delayMillis: Int = 0,
    content: @Composable () -> Unit
) {
    var isVisible by remember(key) { mutableStateOf(false) }

    LaunchedEffect(key) {
        if (key != null) {
            delay(delayMillis.toLong())
            isVisible = true
        } else {
            isVisible = true // Eğer key yoksa direkt göster (eski mesajlar için)
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 250,
                delayMillis = 0
            )
        ) + scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            initialScale = 0.8f
        ) + slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            initialOffsetY = { it / 4 }
        )
    ) {
        content()
    }
}

