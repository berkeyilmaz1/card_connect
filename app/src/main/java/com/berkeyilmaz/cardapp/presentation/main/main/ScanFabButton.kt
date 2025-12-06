package com.berkeyilmaz.cardapp.presentation.main.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.berkeyilmaz.cardapp.R

@Composable
fun ScanFabButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick, containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            imageVector = Icons.Rounded.CameraAlt,
            contentDescription = stringResource(R.string.scan_button),
            tint = Color.White
        )
    }
}