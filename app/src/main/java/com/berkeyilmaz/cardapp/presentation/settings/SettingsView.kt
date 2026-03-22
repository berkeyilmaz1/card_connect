package com.berkeyilmaz.cardapp.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.AutoAwesome
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.AppTitle
import com.berkeyilmaz.cardapp.domain.settings.LocalLlmModelState
import com.berkeyilmaz.cardapp.domain.settings.model.AppTheme
import com.berkeyilmaz.cardapp.presentation.settings.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    onNavigateBack: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel<SettingsViewModel>()
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val useLocalLlm by viewModel.useLocalLlm.collectAsState()
    val localLlmModelState by viewModel.localLlmModelState.collectAsState()
    val showDownloadDialog by viewModel.showDownloadDialog.collectAsState()
    val analyticsConsent by viewModel.analyticsConsent.collectAsState()

    // SYSTEM modunda ise sistem temasını kontrol et, değilse direkt tema değerini kullan
    val isDarkTheme = when (currentTheme) {
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
        AppTheme.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    // Model indirme dialog'u
    if (showDownloadDialog) {
        ModelDownloadDialog(
            onConfirm = { viewModel.startModelDownload() },
            onDismiss = { viewModel.dismissDownloadDialog() }
        )
    }

    // İndirme durumu dialog'u
    if (localLlmModelState is LocalLlmModelState.Downloading) {
        val progress = (localLlmModelState as LocalLlmModelState.Downloading).progress
        DownloadProgressDialog(
            progress = progress,
            onCancel = { viewModel.cancelModelDownload() }
        )
    }

    // Hata dialog'u
    if (localLlmModelState is LocalLlmModelState.Error) {
        val errorMessage = (localLlmModelState as LocalLlmModelState.Error).message
        ErrorDialog(
            message = errorMessage,
            onDismiss = { viewModel.dismissErrorDialog() }
        )
    }

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
                        checkedTrackColor = MaterialTheme.colorScheme.secondary
                    )
                )
            },
            contentDescription = stringResource(R.string.dark_theme)
        ), SettingsItem(
            leadingIcon = Icons.Rounded.Language,
            title = stringResource(R.string.change_language),
            trailingContent = { LanguageDropdown(viewModel = viewModel) },
            contentDescription = stringResource(R.string.change_language)
        ),
        SettingsItem(
            leadingIcon = Icons.Rounded.Analytics,
            title = stringResource(R.string.analytics_data_collection),
            trailingContent = {
                Switch(
                    checked = analyticsConsent,
                    onCheckedChange = { viewModel.setAnalyticsConsent(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.secondary
                    )
                )
            },
            contentDescription = stringResource(R.string.analytics_data_collection)
        ),
        SettingsItem(
            leadingIcon = Icons.Rounded.AutoAwesome,
            title = stringResource(R.string.use_local_llm),
            subtitle = stringResource(R.string.use_local_llm_subtitle) + "\n" + when (localLlmModelState) {
                is LocalLlmModelState.NotDownloaded -> stringResource(R.string.model_not_downloaded)
                is LocalLlmModelState.Downloading -> stringResource(R.string.model_downloading)
                is LocalLlmModelState.Ready -> stringResource(R.string.model_ready)
                is LocalLlmModelState.Error -> stringResource(R.string.model_error)
            },
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Model hazırsa sil butonu göster
                    if (localLlmModelState is LocalLlmModelState.Ready) {
                        IconButton(
                            onClick = { viewModel.deleteModel() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_model),
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Switch(
                        checked = useLocalLlm,
                        onCheckedChange = { isChecked ->
                            viewModel.setUseLocalLlm(isChecked)
                        },
                        enabled = localLlmModelState !is LocalLlmModelState.Downloading,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            },
            contentDescription = stringResource(R.string.use_local_llm)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_normal))
    ) {
        Row {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            AppTitle(stringResource(R.string.settings))
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small))
        ) {
            items(settingsItems.size) { index ->
                SettingsCard({ SettingsRowItem(item = settingsItems[index]) })
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
fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(R.dimen.spacer_8)),
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
                })
            .padding(dimensionResource(R.dimen.padding_normal)),
        verticalAlignment = Alignment.CenterVertically) {
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
    modifier: Modifier = Modifier, viewModel: SettingsViewModel = hiltViewModel()
) {
    val languages = listOf(
        "en" to stringResource(R.string.english),
        "tr" to stringResource(R.string.turkish),
    )

    val currentLanguage by viewModel.currentLanguage.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    val selectedLanguage = languages.find { it.first == currentLanguage.code } ?: languages[0]

    Box(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            TextButton(
                onClick = { expanded = true },
            ) {
                Text(
                    text = selectedLanguage.second,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            ExposedDropdownMenu(
                expanded = expanded, onDismissRequest = { expanded = false }) {
                languages.forEach { lang ->
                    DropdownMenuItem(text = { Text(lang.second) }, onClick = {
                        expanded = false
                        viewModel.setLanguage(lang.first)
                    })
                }
            }
        }
    }
}

// ==================== DIALOGS ====================

@Composable
fun ModelDownloadDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = stringResource(R.string.download_model_title),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = stringResource(R.string.download_model_description),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.download))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun DownloadProgressDialog(
    progress: Float,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Cannot dismiss while downloading */ },
        icon = {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = stringResource(R.string.downloading_model),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.download_in_progress),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onCancel) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}

@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.error),
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Text(text = message)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}
