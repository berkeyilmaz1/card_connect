package com.berkeyilmaz.cardapp.presentation.main.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.AppTitle
import com.berkeyilmaz.cardapp.core.widgets.CustomAppButton
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.presentation.main.groups.viewmodel.GroupsUiState
import com.berkeyilmaz.cardapp.presentation.main.groups.viewmodel.GroupsViewModel

@Composable
fun GroupsView() {
    val viewModel: GroupsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.fetchContacts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_normal)),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {

        when (uiState) {
            GroupsUiState.Idle -> {}
            GroupsUiState.Loading -> LoadingSection()
            GroupsUiState.Error -> ErrorSection(onClick = {
//                viewModel.fetchContacts()
            })

            is GroupsUiState.Success -> {
                val state = uiState as GroupsUiState.Success

                SuccessSection(
                    mainGroups = state.mainGroups,
                    selectedMainGroup = state.selectedMainGroup,
                    subGroups = state.subGroups,
                    contacts = state.contacts,
                    onMainGroupSelected = { viewModel.onMainGroupSelected(it) }
                )
            }
        }
    }
}


@Composable
fun SuccessSection(
    mainGroups: List<String>,
    selectedMainGroup: String,
    subGroups: List<String>,
    contacts: List<Contact>,
    onMainGroupSelected: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    AppTitle(stringResource(R.string.groups))

    /** --- ANA GROUP CHIP ROW --- (İş, Okul, Etkinlik) */
    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .padding(bottom = 8.dp)
    ) {
        mainGroups.forEach { group ->
            FilterChip(
                selected = selectedMainGroup == group,
                onClick = { onMainGroupSelected(group) },
                label = { Text(group) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(Modifier.width(8.dp))
        }
    }

    /** --- ALT GROUP CHIP ROW --- (MOVE ON, Google, Yazılım Ekibi) */
    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .padding(bottom = 8.dp)
    ) {
        subGroups.forEach { sub ->
            FilterChip(
                selected = false,  // İstersen alt grup selection mantığını da ekleriz
                onClick = { /* TODO: alt grup seçimi istersen buraya ekleriz */ },
                label = { Text(sub) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(Modifier.width(8.dp))
        }
    }

    /** --- CONTACT LIST (SEÇİLEN ANA GRUBA AİT CONTACT’LAR) --- */
    val filteredContacts = contacts.filter { contact ->
        contact.groups.containsKey(selectedMainGroup)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xSmall))
    ) {
        items(filteredContacts) { contact ->
            ContactCard(contact = contact)
        }
    }
}

@Composable
fun ContactCard(contact: Contact) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* TODO: Handle click */ },
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
            // Avatar with gradient background
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.spacer_48))
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    ), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.fullName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Contact info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xxSmall))
            ) {
                Text(
                    text = contact.fullName.ifEmpty { stringResource(R.string.unnamed) },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
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
            text = stringResource(R.string.loading_groups),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ErrorSection(onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "An error occurred while loading groups. Please try again later.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        CustomAppButton(
            text = stringResource(R.string.retry),
            onClick = onClick,
            leadingIcon = Icons.Default.Refresh
        )
    }
}