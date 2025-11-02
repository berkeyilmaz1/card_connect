package com.berkeyilmaz.cardapp.presentation.main.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.CustomAppButton
import com.berkeyilmaz.cardapp.presentation.main.more.widgets.MoreItem
import com.berkeyilmaz.cardapp.presentation.main.more.widgets.MoreListItem

@Composable
fun MoreView(
    onNavigateProfile: () -> Unit,
    onNavigateSettings: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: MoreViewModel = hiltViewModel<MoreViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()

    val moreItems: List<MoreItem> = listOf(
        MoreItem(Icons.Outlined.Person, stringResource(R.string.profile), onNavigateProfile),
        MoreItem(Icons.Outlined.Settings, stringResource(R.string.settings), onNavigateSettings),
    )

    LaunchedEffect(Unit) {
        viewModel.signOutEvent.collect {
            onSignOut()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_normal))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.padding_small)
            )
        ) {
            items(moreItems) { item ->
                MoreListItem(moreItem = item)
            }
        }

        CustomAppButton(
            text = stringResource(R.string.sign_out),
            leadingIcon = Icons.AutoMirrored.Outlined.ExitToApp,
            onClick = { viewModel.signOut() },
            loading = uiState.isLoading,
            containerColor = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.padding_normal)),
        )
    }
}