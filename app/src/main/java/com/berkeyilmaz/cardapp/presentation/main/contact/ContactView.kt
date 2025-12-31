package com.berkeyilmaz.cardapp.presentation.main.contact

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.presentation.main.contact.viewmodel.AnalyzeBottomSheetState
import com.berkeyilmaz.cardapp.presentation.main.contact.viewmodel.ContactUiEvent
import com.berkeyilmaz.cardapp.presentation.main.contact.viewmodel.ContactViewModel
import com.berkeyilmaz.cardapp.presentation.main.contact.widgets.AnalyzeBottomSheetContent
import com.berkeyilmaz.cardapp.presentation.main.contact.widgets.ContactContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactView(
    onContactClick: (String) -> Unit, viewModel: ContactViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val bottomSheetState by viewModel.bottomSheetState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showCancelDialog by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // UI olaylarını dinle
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ContactUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message, duration = SnackbarDuration.Short
                    )
                }
                is ContactUiEvent.ShowCancelConfirmation -> {
                    showCancelDialog = true
                }
            }
        }
    }

    // İzin launcher'ı
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(context, isGranted)
    }

    // İlk açılışta izin kontrolü
    LaunchedEffect(Unit) {
        viewModel.checkAndRequestPermission(
            activityContext = context, onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            })
    }

    ContactContent(
        uiState = uiState, onRequestPermission = {
        permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }, onOpenSettings = {
        openAppSettings(context)
    }, onContactClick = onContactClick
    )

    // Analyze Bottom Sheet
    if (bottomSheetState !is AnalyzeBottomSheetState.Hidden) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onBottomSheetDismissRequest() },
            sheetState = sheetState
        ) {
            AnalyzeBottomSheetContent(
                state = bottomSheetState,
                onDismiss = { viewModel.dismissBottomSheet() },
                onDone = { approvedContacts ->
                    viewModel.onContactsApproved(approvedContacts)
                }
            )
        }
    }

    // Cancel Confirmation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text(text = stringResource(R.string.cancel_analysis_title))
            },
            text = {
                Text(text = stringResource(R.string.cancel_analysis_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        viewModel.confirmCancelAnalysis()
                    }
                ) {
                    Text(stringResource(R.string.yes_cancel))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text(stringResource(R.string.no_continue))
                }
            }
        )
    }
}


private fun openAppSettings(context: android.content.Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}
