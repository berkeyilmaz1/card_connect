package com.berkeyilmaz.cardapp.presentation.main.contact

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContactPage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import kotlinx.coroutines.launch

@Composable
fun ContactView(onContactClick: (String) -> Unit) {
    val viewModel: ContactViewModel = hiltViewModel<ContactViewModel>()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var hasPermission by remember { mutableStateOf(false) }
    var shouldShowRationale by remember { mutableStateOf(false) }
    var permissionRequested by remember { mutableStateOf(false) }

    // Error event'leri dinle
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ContactUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message, duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            val activity = context as? android.app.Activity
            shouldShowRationale = activity?.let {
                !ActivityCompat.shouldShowRequestPermissionRationale(
                    it, Manifest.permission.READ_CONTACTS
                )
            } ?: false
            return@rememberLauncherForActivityResult
        }

        if (uiState !is ContactUiState.Success) {
            coroutineScope.launch {
                viewModel.getContacts(context.contentResolver)
            }
        }
    }

    LaunchedEffect(Unit) {
        val isPermissionGranted =
            context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        hasPermission = isPermissionGranted

        if (isPermissionGranted && uiState !is ContactUiState.Success) {
            coroutineScope.launch {
                viewModel.getContacts(context.contentResolver)
            }
        } else if (!isPermissionGranted && !permissionRequested) {
            permissionRequested = true
            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_normal)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        when {
            !hasPermission -> {
                PermissionSection(shouldShowRationale = shouldShowRationale, onRequestPermission = {
                    permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }, onOpenSettings = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                })
            }

            uiState is ContactUiState.Loading -> {
                LoadingSection()
            }

            uiState is ContactUiState.Success -> {
                val contacts = (uiState as ContactUiState.Success).contacts
                if (contacts.isEmpty()) {
                    EmptyContactsSection()
                } else {
                    ContactList(contacts)
                }
            }

            uiState is ContactUiState.Idle -> {
            }
        }
    }

}

@Composable
fun PermissionSection(
    shouldShowRationale: Boolean, onRequestPermission: () -> Unit, onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.spacer_96))
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
        ) {
            Icon(
                imageVector = Icons.Outlined.ContactPage,
                contentDescription = stringResource(R.string.scanner_icon),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.spacer_48))
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_normal)))

        Text(
            text = stringResource(R.string.contacts_permission_is_required_to_display_contacts),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_normal))
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_xSmall)))

        Text(
            text = stringResource(R.string.please_grant_the_permission_to_continue),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_lowNormal)),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))


        if (shouldShowRationale) {
            Button(
                onClick = onOpenSettings,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_normal))
            ) {
                Text(text = stringResource(R.string.open_settings))
            }
        } else {
            Button(
                onClick = onRequestPermission,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_normal))
            ) {
                Text(text = stringResource(R.string.please_grant_the_permission_to_continue))
            }
        }
    }
}

@Composable
fun LoadingSection() {
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
fun EmptyContactsSection() {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.spacer_96))
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
        ) {
            Icon(
                imageVector = Icons.Outlined.ContactPage,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.spacer_48))
                    .align(Alignment.Center)
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

@Composable
fun ContactList(contacts: List<Contact>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xSmall))
    ) {
        items(contacts) { contact ->
            ContactCard(contact = contact)
        }
    }
}

@Composable
fun ContactCard(contact: Contact) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* TODO: Handle click */ },
        tonalElevation = dimensionResource(R.dimen.elevation_xSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_normal)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_normal))
        ) {
            // Avatar with first letter
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.icon_size_large))
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name?.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Contact info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xxSmall))
            ) {
                Text(
                    text = contact.name ?: stringResource(R.string.unnamed),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )

                if (contact.phoneNumbers.isNotEmpty()) {
                    Text(
                        text = contact.phoneNumbers.first(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}