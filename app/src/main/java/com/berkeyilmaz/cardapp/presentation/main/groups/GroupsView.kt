package com.berkeyilmaz.cardapp.presentation.main.groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.CustomAppButton
import com.berkeyilmaz.cardapp.presentation.main.groups.viewmodel.GroupsUiState
import com.berkeyilmaz.cardapp.presentation.main.groups.viewmodel.GroupsViewModel
import com.berkeyilmaz.cardapp.presentation.main.groups.widgets.SuccessSection

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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {

        when (uiState) {
            GroupsUiState.Idle -> {}
            GroupsUiState.Loading -> LoadingSection()
            GroupsUiState.Error -> ErrorSection(onClick = {
                viewModel.fetchContacts()
            })

            is GroupsUiState.Success -> {
                val state = uiState as GroupsUiState.Success

                SuccessSection(
                    mainGroups = state.mainGroups,
                    selectedMainGroup = state.selectedMainGroup,
                    subGroups = state.subGroups,
                    selectedSubGroup = state.selectedSubGroup,
                    contacts = state.contacts,
                    onMainGroupSelected = { viewModel.onMainGroupSelected(it) },
                    onSubGroupSelected = { viewModel.onSubGroupSelected(it) })
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
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.error_fetching_groups),
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