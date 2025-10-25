package com.berkeyilmaz.cardapp.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.domain.theme.model.AppTheme
import com.berkeyilmaz.cardapp.presentation.settings.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    onNavigateBack: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val isDarkTheme = currentTheme == AppTheme.DARK

    val settingsItems: List<SettingsItem> = listOf(
        SettingsItem(
            leadingIcon = if (isDarkTheme) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
            title = stringResource(R.string.dark_theme),
            trailingContent = {
                Switch(
                    checked = isDarkTheme, onCheckedChange = { isChecked ->
                        viewModel.setTheme(
                            if (isChecked) AppTheme.DARK else AppTheme.LIGHT
                        )
                    }, colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.secondary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                )
            },
            contentDescription = stringResource(R.string.dark_theme)
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(dimensionResource(R.dimen.padding_normal)),
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small))
        ) {
            items(settingsItems.size) {
                SettingsSection(stringResource(R.string.appearance)) {
                    SettingsRowItem(item = settingsItems[it])
                }
            }
        }
    }
}

data class SettingsItem(
    val leadingIcon: ImageVector,
    val title: String,
    val subtitle: String? = null,
    val trailingIcon: ImageVector? = null,
    val trailingContent: @Composable (() -> Unit)? = null,
    val onClick: (() -> Unit)? = null,
    var contentDescription: String
)

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.padding_normal),
                vertical = dimensionResource(R.dimen.padding_small)
            )
        )
        SettingsCard {
            content()
        }
    }
}

@Composable
fun SettingsCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ), elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ), shape = RoundedCornerShape(dimensionResource(R.dimen.padding_lowNormal))
    ) {
        content()
    }
}

@Composable
fun SettingsRowItem(
    item: SettingsItem
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (item.onClick != null) {
                    Modifier.clickable { item.onClick() }
                } else {
                    Modifier
                })
            .padding(dimensionResource(R.dimen.padding_normal)),
        verticalAlignment = Alignment.CenterVertically) {
        // Leading Icon
        Icon(
            imageVector = item.leadingIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_normal)))

        // Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Normal
            )

            item.subtitle?.let {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_2)))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Trailing Content
        when {
            item.trailingContent != null -> {
                item.trailingContent()
            }

            item.trailingIcon != null -> {
                Icon(
                    imageVector = item.trailingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            else -> {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}