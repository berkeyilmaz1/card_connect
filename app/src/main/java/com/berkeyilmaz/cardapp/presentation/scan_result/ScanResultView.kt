package com.berkeyilmaz.cardapp.presentation.scan_result

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import com.berkeyilmaz.cardapp.presentation.scan_result.viewmodel.ScanResultViewModel
import com.berkeyilmaz.cardapp.presentation.scan_result.widgets.ScanResultContent
import kotlinx.coroutines.launch

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
        scanResponse?.let { data ->
            viewModel.apply {
                scanResponse.fullName?.let { updateFullName(it) }
                scanResponse.title?.let { updateJobTitle(it) }
                scanResponse.organization?.let { updateCompany(it) }
                updatePhones(scanResponse.phones)
                updateEmails(scanResponse.emails)
                updateWebsites(scanResponse.websites)
                scanResponse.address?.let { updateAddresses(it) }
                updateSocialMedia(scanResponse.socialMedias)
                updateTags(scanResponse.tags)
                scanResponse.note?.let { updateNotes(it) }
                updateImage(scanResponse.imageUrl.orEmpty())
            }
        }
    }
}



