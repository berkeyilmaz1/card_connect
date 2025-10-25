package com.berkeyilmaz.cardapp.presentation.main.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.presentation.main.more.widgets.MoreItem
import com.berkeyilmaz.cardapp.presentation.main.more.widgets.MoreListItem

@Composable
fun MoreView(onNavigateProfile: () -> Unit, onNavigateSettings: () -> Unit) {
    val moreItems: List<MoreItem> = listOf(
        MoreItem(Icons.Outlined.Person, stringResource(R.string.profile), onNavigateProfile),
        MoreItem(Icons.Outlined.Settings, stringResource(R.string.settings), onNavigateSettings),
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_normal)),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.padding_small)
        )
    ) {
        items(moreItems) { item ->
            MoreListItem(moreItem = item)
        }
    }
}