package com.berkeyilmaz.cardapp.presentation.main.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.berkeyilmaz.cardapp.R

@Composable
fun AiFabButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick, containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            imageVector = Icons.Rounded.AutoAwesome,
            contentDescription = stringResource(R.string.scan_button),
            tint = Color.White
        )
    }
}