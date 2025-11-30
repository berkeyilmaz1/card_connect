package com.berkeyilmaz.cardapp.presentation.scan_result

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.SubcomposeAsyncImage
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.CustomAppButton
import com.berkeyilmaz.cardapp.core.widgets.CustomTextField
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResultRowItem
import com.berkeyilmaz.cardapp.presentation.scan_result.viewmodel.ScanResultViewModel
import kotlinx.coroutines.launch

@Composable
fun ScanResultView(
    scanResponse: ScanResponse?, onNavigateAfterSave: () -> Unit = {}
) {

    val viewModel = hiltViewModel<ScanResultViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration =
                        SnackbarDuration.Short
                )
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.createContact()
        } else {
            Log.w("ScanResultView", "WRITE_CONTACTS izni reddedildi")
            viewModel.createContact()
        }
    }

    val resultItems = listOf(
        ScanResultRowItem(
            title = stringResource(R.string.full_name), content = {
                CustomTextField(
                    value = uiState.fullName.orEmpty(),
                    onValueChange = { viewModel.updateFullName(it) },
                    leadingIcon = Icons.Default.Person,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                )
            }),
        ScanResultRowItem(
            title = stringResource(R.string.job_title), content = {
                CustomTextField(
                    value = uiState.jobTitle.orEmpty(),
                    onValueChange = { viewModel.updateJobTitle(it) },
                    leadingIcon = Icons.Default.Lock,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                )
            }),
        ScanResultRowItem(
            title = stringResource(R.string.company), content = {
                CustomTextField(
                    value = uiState.company.orEmpty(),
                    onValueChange = { viewModel.updateCompany(it) },
                    leadingIcon = Icons.Default.Business,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                )
            }),
        ScanResultRowItem(
            title = stringResource(R.string.phone_number), content = {
                CustomTextField(
                    value = uiState.phoneNumber.orEmpty(),
                    onValueChange = { viewModel.updatePhoneNumber(it) },
                    leadingIcon = Icons.Default.Phone,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next,
                )
            }),
        ScanResultRowItem(
            title = stringResource(R.string.email), content = {
                CustomTextField(
                    value = uiState.email.orEmpty(),
                    onValueChange = { viewModel.updateEmail(it) },
                    leadingIcon = Icons.Default.Email,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                )
            }),
        ScanResultRowItem(
            title = stringResource(R.string.address), content = {
                CustomTextField(
                    value = uiState.address.orEmpty(),
                    onValueChange = { viewModel.updateAddress(it) },
                    leadingIcon = Icons.Default.LocationOn,
                    singleLine = false,
                    maxLines = 3,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                )
            }),
        ScanResultRowItem(
            title = stringResource(R.string.tags), content = {
                CustomTextField(
                    value = uiState.tags.orEmpty().joinToString(", "),
                    onValueChange = {
                        viewModel.updateTags(
                            it.split(",").map { tag -> tag.trim() }.filter { tag -> tag.isNotEmpty() }
                        )
                    },
                    leadingIcon = Icons.Default.Tag,
                    singleLine = false,
                    maxLines = 3,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                )
            }),
        ScanResultRowItem(
            title = "Websites", content = {
                CustomTextField(
                    value = uiState.websites.orEmpty(),
                    onValueChange = { viewModel.updateWebsites(it) },
                    leadingIcon = Icons.Default.Web,
                    singleLine = false,
                    maxLines = 3,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                )
            }),
        ScanResultRowItem(
            title = "Social Media Accounts", content = {
                CustomTextField(
                    value = uiState.socialMedia.orEmpty().joinToString(", ") { "${it.platform?.platformName ?: "Unknown"}: ${it.url}" },
                    onValueChange = {
                        viewModel.updateSocialMedia(uiState.socialMedia ?: emptyList())
                    },
                    leadingIcon = Icons.Default.Business,
                    singleLine = false,
                    maxLines = 3,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                )
            }),
        ScanResultRowItem(
            title = stringResource(R.string.notes), content = {
                CustomTextField(
                    value = uiState.notes.orEmpty(),
                    onValueChange = { viewModel.updateNotes(it) },
                    singleLine = false,
                    minLines = 5,
                    maxLines = 10,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                )
            }),
    )

    LaunchedEffect(scanResponse) {
        Log.i("BerkeTAG", "Populating ViewModel with scan response: $scanResponse")
        scanResponse?.let {
            viewModel.updateFullName(it.extractedData?.fullName.orEmpty())
            viewModel.updateJobTitle(it.extractedData?.jobTitle.orEmpty())
            viewModel.updateCompany(it.extractedData?.organization.orEmpty())
            viewModel.updatePhoneNumber(it.extractedData?.phones?.firstOrNull().orEmpty())
            viewModel.updateEmail(it.extractedData?.emails?.firstOrNull().orEmpty())
            viewModel.updateAddress(it.extractedData?.addresses?.firstOrNull().orEmpty())
            viewModel.updateNotes(it.extractedData?.note.orEmpty())
            viewModel.updateWebsites(it.extractedData?.websites?.firstOrNull().orEmpty())
            viewModel.updateTags(it.extractedData?.tags.orEmpty())
            viewModel.updateImage(it.imageUrl.orEmpty())
            viewModel.updateRawText(it.rawText.orEmpty())
            viewModel.updateSocialMedia(it.extractedData?.socialMedia ?: emptyList())
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateAfterSave()
            viewModel.resetSavedState()
        }
    }

    Scaffold(
        snackbarHost = {
            androidx.compose.material3.SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            CustomAppButton(
                text = stringResource(R.string.save),
                onClick = {
                    permissionLauncher.launch(Manifest.permission.WRITE_CONTACTS)
                },
                loading = uiState.isLoading,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(R.dimen.padding_normal),
                        vertical = dimensionResource(R.dimen.padding_small)
                    )
            )
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.padding_normal))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_small)
                )
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close, contentDescription = stringResource(
                                R.string.close
                            ), modifier = Modifier.clickable(enabled = true, onClick = {
                                // Kapatma işlemi burada gerçekleştirilecek
                            })
                        )
                    }
                }
                item { ScannedCard(imagePath = uiState.image.orEmpty()) }

                items(resultItems) { item ->
                    ResultSection(title = item.title, content = item.content)
                }
            }

        }
    }
}


@Composable
fun ResultSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title, style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_4)))
        content()
    }
}

@Composable
fun ScannedCard(imagePath: String = "") {
    val cardImage = ScanResultRowItem(
        content = {
            if (imagePath.isNotEmpty()) {
                SubcomposeAsyncImage(
                    model = java.io.File(imagePath),
                    contentDescription = stringResource(R.string.scanned_card),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Fit,
                    loading = {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            androidx.compose.material3.CircularProgressIndicator()
                        }
                    },
                    error = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_background),
                            contentDescription = stringResource(R.string.scanned_card),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Fit
                        )
                    })
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = stringResource(R.string.scanned_card),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Fit
                )
            }
        })

    Column {
        ResultSection(title = cardImage.title, content = cardImage.content)
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_8)))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_4)))

    }
}

