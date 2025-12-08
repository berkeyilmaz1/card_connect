package com.berkeyilmaz.cardapp.presentation.main.scan

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.presentation.main.scan.viewmodel.ScanUiState
import com.berkeyilmaz.cardapp.presentation.main.scan.viewmodel.ScanViewModel
import com.berkeyilmaz.cardapp.presentation.main.scan.widgets.CameraPreview
import com.google.gson.Gson
import java.io.File

@Composable
fun ScanView(
    onScanCompleted: (String) -> Unit = {},
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: ScanViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Camera permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    // Request permission if needed
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Camera controller
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS)
        }
    }

    // Bind camera lifecycle
    DisposableEffect(lifecycleOwner) {
        cameraController.bindToLifecycle(lifecycleOwner)
        onDispose {
            try { cameraController.unbind() } catch (e: Exception) { Log.e("CameraX", "Error unbinding camera", e) }
        }
    }

    // Handle successful scan
    LaunchedEffect(uiState) {
        (uiState as? ScanUiState.Success)?.data?.let { scanResponse ->
            val json = Uri.encode(Gson().toJson(scanResponse))
            Log.i("BerkeTag", "Scan successful: $json")
            onScanCompleted(json)
        }
    }

    // UI
    when {
        !hasCameraPermission -> {
            Text(
                text = stringResource(R.string.camera_permission_required),
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_normal))
            )
        }
        uiState is ScanUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f))
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        else -> {
            CameraPreview(
                cameraController = cameraController,
                onTakePhoto = {
                    viewModel.takePhoto(cameraController, context) { uri ->
                        uri?.path?.let { path -> viewModel.scanImageOnDevice(File(path)) }
                    }
                },
                onBack = onBackPressed
            )
        }
    }
}

