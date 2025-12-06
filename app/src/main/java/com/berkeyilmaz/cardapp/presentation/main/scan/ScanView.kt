package com.berkeyilmaz.cardapp.presentation.main.scan


import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.presentation.main.scan.viewmodel.ScanUiState
import com.berkeyilmaz.cardapp.presentation.main.scan.viewmodel.ScanViewModel
import com.google.gson.Gson
import java.io.File


@Composable
fun ScanView(
    onScanCompleted: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel = hiltViewModel<ScanViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) launcher.launch(Manifest.permission.CAMERA)
    }
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS
            )
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is ScanUiState.Success) {
            val scanResponse = (uiState as ScanUiState.Success).data
            val json = Uri.encode(Gson().toJson(scanResponse))
            Log.i("BerkeTag", "Scan successful: $json")
            onScanCompleted(json)
        }
    }

    DisposableEffect(lifecycleOwner) {
        cameraController.bindToLifecycle(lifecycleOwner)

        onDispose {
            try {
                cameraController.unbind()
            } catch (e: Exception) {
                Log.e("CameraX", "Error unbinding camera", e)
            }
        }
    }

    if (hasCameraPermission) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Preview
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        controller = cameraController
                    }
                }, modifier = Modifier.fillMaxSize()
            )

            // Fotoğraf çekme butonu
            FloatingActionButton(
                onClick = {
                    viewModel.takePhoto(cameraController, context) { uri ->
                        uri?.let {
                            val file = File(it.path!!)
//                            viewModel.scanImage(file)
                            viewModel.scanImageOnDevice(file)
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Rounded.CameraAlt,
                    contentDescription = stringResource(R.string.capture),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Geri butonu
            IconButton(
                onClick = { (context as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed() },
                modifier = Modifier
                    .statusBarsPadding()
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Loading overlay - kamera preview'ının üstünde
            if (uiState is ScanUiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) { /* Tıklamaları engelle */ },
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    } else {
        Text(
            text = stringResource(R.string.camera_permission_required), modifier = Modifier.padding(
                dimensionResource(R.dimen.padding_normal)
            )
        )
    }

}
