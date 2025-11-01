package com.berkeyilmaz.cardapp.presentation.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.domain.settings.model.AppTheme
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
                    checked = isDarkTheme,
                    onCheckedChange = { isChecked ->
                        viewModel.setTheme(
                            if (isChecked) AppTheme.DARK else AppTheme.LIGHT
                        )
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.secondary
                    )
                )
            },
            contentDescription = stringResource(R.string.dark_theme)
        ),
        SettingsItem(
            leadingIcon = Icons.Rounded.Language,
            title = stringResource(R.string.change_language),
            trailingContent = { LanguageDropdown(viewModel = viewModel) },
            contentDescription = stringResource(R.string.change_language)
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
                    IconButton(onClick = onNavigateBack) {
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
            items(settingsItems.size) { index ->
                SettingsSection(stringResource(R.string.appearance)) {
                    SettingsRowItem(item = settingsItems[index])
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
    val contentDescription: String
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
fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.padding_lowNormal))
    ) {
        content()
    }
}

@Composable
fun SettingsRowItem(item: SettingsItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (item.onClick != null) {
                    Modifier.clickable { item.onClick() }
                } else {
                    Modifier
                }
            )
            .padding(dimensionResource(R.dimen.padding_normal)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.leadingIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_normal)))

        Column(modifier = Modifier.weight(1f)) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageDropdown(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val languages = listOf(
        "en" to stringResource(R.string.english),
        "tr" to stringResource(R.string.turkish),
    )

    val currentLanguage by viewModel.currentLanguage.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    // Mevcut dili bul
    val selectedLanguage = languages.find { it.first == currentLanguage.code } ?: languages[0]

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedLanguage.second,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.language)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                languages.forEach { lang ->
                    DropdownMenuItem(
                        text = { Text(lang.second) },
                        onClick = {
                            expanded = false
                            viewModel.setLanguage(lang.first)
                        }
                    )
                }
            }
        }
    }
}