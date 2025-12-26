package com.berkeyilmaz.cardapp.presentation.main.contact.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.ContactPage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.AppTitle
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import com.berkeyilmaz.cardapp.presentation.main.contact.viewmodel.ContactUiState

@Composable
fun ContactContent(
    uiState: ContactUiState,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onContactClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_normal)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        when (uiState) {
            is ContactUiState.Idle -> {
            }

            is ContactUiState.PermissionDenied -> {
                PermissionSection(
                    isPermanentlyDenied = uiState.isPermanentlyDenied,
                    onRequestPermission = onRequestPermission,
                    onOpenSettings = onOpenSettings
                )
            }

            is ContactUiState.Loading -> {
                LoadingSection()
            }

            is ContactUiState.Success -> {
                if (uiState.contacts.isEmpty()) {
                    EmptyContactsSection()
                } else {
                    ContactList(
                        contacts = uiState.contacts, onContactClick = onContactClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactList(
    contacts: List<InternalContact>, onContactClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppTitle(stringResource(R.string.contacts))

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_xSmall)))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xSmall))
        ) {
            items(
                items = contacts, key = { contact -> contact.internalId }) { contact ->
                ContactCard(
                    contact = contact, onClick = {
                        contact.phoneNumbers.firstOrNull()?.let { phoneNumber ->
                            onContactClick(phoneNumber)
                        }
                    })
            }
        }
    }
}

@Composable
private fun ContactCard(
    contact: InternalContact, onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(R.dimen.padding_normal)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.elevation_xSmall)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_normal)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_normal))
        ) {
            ContactAvatar(name = contact.fullName)

            ContactInfo(
                name = contact.fullName, phoneNumber = contact.phoneNumbers.firstOrNull() ?: ""
            )
        }
    }
}

@Composable
private fun ContactAvatar(name: String) {
    Box(
        modifier = Modifier
            .size(dimensionResource(R.dimen.spacer_48))
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary
                    )
                )
            ), contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.firstOrNull()?.uppercase() ?: "?",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun ContactInfo(
    name: String, phoneNumber: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xxSmall))
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (phoneNumber.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xxSmall))
            ) {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(R.dimen.spacer_16)),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LoadingSection() {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_normal)))

        Text(
            text = stringResource(R.string.loading_contacts),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun EmptyContactsSection() {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.spacer_96))
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ContactPage,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(dimensionResource(R.dimen.spacer_48))
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_normal)))

        Text(
            text = stringResource(R.string.no_contacts_found),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}