package com.berkeyilmaz.cardapp.presentation.scan_result

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.SubcomposeAsyncImage
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.CustomAppButton
import com.berkeyilmaz.cardapp.core.widgets.CustomTextField
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResultRowItem
import com.berkeyilmaz.cardapp.domain.scan_result.model.Tag
import com.berkeyilmaz.cardapp.presentation.scan_result.viewmodel.ScanResultState
import com.berkeyilmaz.cardapp.presentation.scan_result.viewmodel.ScanResultViewModel
import com.berkeyilmaz.cardapp.presentation.scan_result.widgets.ScanResultContent
import com.berkeyilmaz.cardapp.presentation.scan_result.widgets.ScannedCardImage
import com.berkeyilmaz.cardapp.presentation.scan_result.widgets.TagsSection
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ScanResultView(
    scanResponse: ScanResponse?,
    onNavigateAfterSave: () -> Unit = {},
    onClose: () -> Unit,
    viewModel: ScanResultViewModel = hiltViewModel<ScanResultViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Handle snackbar messages
    HandleSnackbarMessages(
        successMessage = uiState.successMessage,
        errorMessage = uiState.errorMessage,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope
    )

    // Handle permission request
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.createContact()
    }

    // Initialize with scan response
    InitializeScanData(scanResponse, viewModel)

    // Handle navigation after save
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateAfterSave()
            viewModel.resetSavedState()
        }
    }

    ScanResultContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onSaveClick = { permissionLauncher.launch(Manifest.permission.WRITE_CONTACTS) },
        onClose = onClose,
        viewModel = viewModel
    )
}

@Composable
private fun HandleSnackbarMessages(
    successMessage: String?,
    errorMessage: String?,
    snackbarHostState: SnackbarHostState,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = message, duration = SnackbarDuration.Short
                )
            }
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = message, duration = SnackbarDuration.Long
                )
            }
        }
    }
}

@Composable
private fun InitializeScanData(
    scanResponse: ScanResponse?, viewModel: ScanResultViewModel
) {
    LaunchedEffect(scanResponse) {
        scanResponse?.extractedData?.let { data ->
            viewModel.apply {
                updateFullName(data.fullName.orEmpty())
                updateJobTitle(data.jobTitle.orEmpty())
                updateCompany(data.organization.orEmpty())
                updatePhoneNumber(data.phones.firstOrNull().orEmpty())
                updateEmail(data.emails.firstOrNull().orEmpty())
                updateAddress(data.addresses.firstOrNull().orEmpty())
                updateNotes(data.note.orEmpty())
                updateWebsites(data.websites.firstOrNull().orEmpty())
                updateTags(data.tags)
                updateSocialMedia(data.socialMedia)
            }
        }
        scanResponse?.let {
            viewModel.updateImage(it.imageUrl.orEmpty())
            viewModel.updateRawText(it.rawText.orEmpty())
        }
    }
}



