package com.berkeyilmaz.cardapp.presentation.scan_result.widgets

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.CustomAppButton
import com.berkeyilmaz.cardapp.core.widgets.CustomTextField
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResultRowItem
import com.berkeyilmaz.cardapp.presentation.scan_result.viewmodel.ScanResultState
import com.berkeyilmaz.cardapp.presentation.scan_result.viewmodel.ScanResultViewModel
import kotlin.collections.any
import kotlin.collections.filter
import kotlin.collections.orEmpty
import kotlin.collections.plus

@Composable
fun ScanResultContent(
    uiState: ScanResultState,
    snackbarHostState: SnackbarHostState,
    onSaveClick: () -> Unit,
    onClose: () -> Unit,
    viewModel: ScanResultViewModel
) {
    val context = LocalContext.current
    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, bottomBar = {
        SaveButton(
            isLoading = uiState.isLoading, onClick = onSaveClick
        )
    }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.padding_normal)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            item {
                CloseButton(onClick = onClose)
            }

            item {
                ScannedCardImage(imagePath = uiState.image.orEmpty())
            }

            items(createResultItems(uiState, viewModel, context = context)) { item ->
                ResultSection(title = item.title, content = item.content)
            }
        }
    }
}

@Composable
private fun CloseButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.close)
            )
        }
    }
}

@Composable
private fun SaveButton(
    isLoading: Boolean, onClick: () -> Unit
) {
    CustomAppButton(
        text = stringResource(R.string.save),
        onClick = onClick,
        loading = isLoading,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.padding_normal),
                vertical = dimensionResource(R.dimen.padding_small)
            )
    )
}

private fun createResultItems(
    uiState: ScanResultState, viewModel: ScanResultViewModel, context: Context
): List<ScanResultRowItem> {
    val fullNameTitle = context.getString(R.string.full_name)
    val jobTitleTitle = context.getString(R.string.job_title)
    val companyTitle = context.getString(R.string.company)
    val phoneTitle = context.getString(R.string.phone_number)
    val emailTitle = context.getString(R.string.email)
    val addressTitle = context.getString(R.string.address)
    val tagsTitle = context.getString(R.string.tags)
    val websitesTitle = context.getString(R.string.websites)
    val socialMediaTitle = context.getString(R.string.social_media)
    val notesTitle = context.getString(R.string.notes)

    return listOf(
        ScanResultRowItem(
            title = fullNameTitle, content = {
                CustomTextField(
                    value = uiState.fullName.orEmpty(),
                    onValueChange = viewModel::updateFullName,
                    leadingIcon = Icons.Default.Person,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            }), ScanResultRowItem(
            title = jobTitleTitle, content = {
                CustomTextField(
                    value = uiState.jobTitle.orEmpty(),
                    onValueChange = viewModel::updateJobTitle,
                    leadingIcon = Icons.Default.Lock,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            }), ScanResultRowItem(
            title = companyTitle, content = {
                CustomTextField(
                    value = uiState.company.orEmpty(),
                    onValueChange = viewModel::updateCompany,
                    leadingIcon = Icons.Default.Business,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            }), ScanResultRowItem(
            title = phoneTitle, content = {
                CustomTextField(
                    value = uiState.phoneNumber.orEmpty(),
                    onValueChange = viewModel::updatePhoneNumber,
                    leadingIcon = Icons.Default.Phone,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            }), ScanResultRowItem(
            title = emailTitle, content = {
                CustomTextField(
                    value = uiState.email.orEmpty(),
                    onValueChange = viewModel::updateEmail,
                    leadingIcon = Icons.Default.Email,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            }), ScanResultRowItem(
            title = addressTitle, content = {
                CustomTextField(
                    value = uiState.address.orEmpty(),
                    onValueChange = viewModel::updateAddress,
                    leadingIcon = Icons.Default.LocationOn,
                    singleLine = false,
                    maxLines = 3,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            }), ScanResultRowItem(
            title = tagsTitle, content = {
                TagsSection(tags = uiState.tags.orEmpty(), onTagRemove = { tag ->
                    viewModel.updateTags(uiState.tags.orEmpty().filter { it != tag })
                }, onTagAdd = { newTag ->
                    val exists = uiState.tags.orEmpty().any { it.name == newTag.name }
                    if (!exists) {
                        viewModel.updateTags(uiState.tags.orEmpty() + newTag)
                    }
                })
            }), ScanResultRowItem(
            title = websitesTitle, content = {
                CustomTextField(
                    value = uiState.websites.orEmpty(),
                    onValueChange = viewModel::updateWebsites,
                    leadingIcon = Icons.Default.Web,
                    singleLine = false,
                    maxLines = 3,
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                )
            }), ScanResultRowItem(
            title = socialMediaTitle, content = {
                CustomTextField(
                    value = formatSocialMedia(uiState.socialMedia),
                    onValueChange = {
                        viewModel.updateSocialMedia(
                            uiState.socialMedia ?: emptyList()
                        )
                    },
                    leadingIcon = Icons.Default.Business,
                    singleLine = false,
                    maxLines = 3,
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                )
            }), ScanResultRowItem(
            title = notesTitle, content = {
                CustomTextField(
                    value = uiState.notes.orEmpty(),
                    onValueChange = viewModel::updateNotes,
                    singleLine = false,
                    minLines = 5,
                    maxLines = 10,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                )
            })
    )
}

private fun formatSocialMedia(socialMedia: List<*>?): String {
    return socialMedia?.joinToString(", ") { media ->
        val platform = media?.let {
            it::class.java.getMethod("getPlatform").invoke(it)
        }
        val platformName = platform?.let {
            it::class.java.getMethod("getPlatformName").invoke(it) as? String
        } ?: "Unknown"
        val url = media?.let {
            it::class.java.getMethod("getUrl").invoke(it) as? String
        } ?: ""
        "$platformName: $url"
    }.orEmpty()
}

@Composable
fun ResultSection(
    title: String, content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title, style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_4)))
        content()
    }
}
