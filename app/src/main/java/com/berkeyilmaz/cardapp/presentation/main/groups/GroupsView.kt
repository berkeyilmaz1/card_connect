package com.berkeyilmaz.cardapp.presentation.main.groups

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.AppTitle
import com.berkeyilmaz.cardapp.core.widgets.CustomAppButton
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.presentation.main.contact.ContactCard
import com.berkeyilmaz.cardapp.presentation.main.groups.viewmodel.GroupsUiState
import com.berkeyilmaz.cardapp.presentation.main.groups.viewmodel.GroupsViewModel

@Composable
fun GroupsView() {
    val viewModel: GroupsViewModel = hiltViewModel<GroupsViewModel>()
    val uiState by viewModel.uiState.collectAsState()

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
            GroupsUiState.Error -> ErrorSection(onClick = { viewModel.fetchContacts() })
            is GroupsUiState.Success -> SuccessSection(
                groups = (uiState as GroupsUiState.Success).groups,
                contacts = (uiState as GroupsUiState.Success).contacts.filter { contact ->
                    contact.tags?.any { tag ->
                        tag.name == (uiState as GroupsUiState.Success).selectedGroup
                    } ?: false
                },
                selectedGroup = (uiState as GroupsUiState.Success).selectedGroup,
                onGroupSelected = { group ->
                    viewModel.onGroupSelected(group)
                })
        }
    }
}

@Composable
fun SuccessSection(
    groups: List<String>,
    contacts: List<Contact>,
    selectedGroup: String,
    onGroupSelected: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    AppTitle(stringResource(R.string.groups))
    Row(
        modifier = Modifier.horizontalScroll(scrollState),
    ) {
        groups.forEach { group ->
            FilterChip(
                selected = selectedGroup == group,
                onClick = { onGroupSelected(group) },
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
    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_xSmall)))
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