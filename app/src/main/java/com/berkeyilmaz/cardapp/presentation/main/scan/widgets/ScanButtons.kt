package com.berkeyilmaz.cardapp.presentation.main.scan.widgets

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.berkeyilmaz.cardapp.R

@Composable
fun BoxScope.ScanFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 32.dp),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            Icons.Rounded.CameraAlt,
            contentDescription = stringResource(R.string.capture),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}


@Composable
fun BoxScope.BackButton(onClick: () -> Unit) {
    IconButton(
        onClick = { onClick() }, modifier = Modifier
            .statusBarsPadding()
            .align(Alignment.TopStart)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}