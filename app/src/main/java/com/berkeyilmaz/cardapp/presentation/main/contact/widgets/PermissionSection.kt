package com.berkeyilmaz.cardapp.presentation.main.contact.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContactPage
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.berkeyilmaz.cardapp.R

@Composable
fun PermissionSection(
    isPermanentlyDenied: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PermissionIcon()

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_normal)))

        Text(
            text = stringResource(R.string.contacts_permission_is_required_to_display_contacts),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_normal))
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_xSmall)))

        Text(
            text = stringResource(R.string.please_grant_the_permission_to_continue),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_lowNormal))
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))

        PermissionButton(
            isPermanentlyDenied = isPermanentlyDenied,
            onRequestPermission = onRequestPermission,
            onOpenSettings = onOpenSettings
        )
    }
}

@Composable
private fun PermissionIcon() {
    Box(
        modifier = Modifier
            .size(dimensionResource(R.dimen.spacer_96))
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ContactPage,
            contentDescription = stringResource(R.string.scanner_icon),
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(dimensionResource(R.dimen.spacer_48))
        )
    }
}

@Composable
private fun PermissionButton(
    isPermanentlyDenied: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Button(
        onClick = if (isPermanentlyDenied) onOpenSettings else onRequestPermission,
        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_normal))
    ) {
        Text(
            text = stringResource(
                if (isPermanentlyDenied) {
                    R.string.open_settings
                } else {
                    R.string.please_grant_the_permission_to_continue
                }
            )
        )
    }
}