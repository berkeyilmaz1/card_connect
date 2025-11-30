package com.berkeyilmaz.cardapp.presentation.main.main.scan.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.domain.scan.usecase.ScanUseCase
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

sealed class ScanUiState {
    object Idle : ScanUiState()
    object Loading : ScanUiState()
    data class Success(val data: ScanResponse) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanUseCase: ScanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun takePhoto(
        controller: LifecycleCameraController, context: Context, onResult: (Uri?) -> Unit
    ) {
        val scanImagesDir = File(context.filesDir, "scan_images")
        if (!scanImagesDir.exists()) {
            scanImagesDir.mkdirs()
        }

        val outputFile = File(scanImagesDir, "scan_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

        controller.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("BerkeTAG", "Photo capture failed: ${exc.message}", exc)
                    onResult(null)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.i("BerkeTAG", "Photo capture succeeded: ${outputFile.absolutePath}")
                    onResult(Uri.fromFile(outputFile))
                }
            })
    }

    fun scanImage(file: File) {
        viewModelScope.launch {
            setLoading()
            val fileSizeKb = file.length() / 1024
            Log.d("BerkeTAG", "Image size: ${fileSizeKb}KB")

            val result = scanUseCase(file)
            Log.i("BerkeTAG", "Scan result: $result")
            result.fold(onSuccess = { data ->

                val updatedData = data.copy(imageUrl = file.absolutePath)
                setSuccess(updatedData)
            }, onFailure = { error ->
                withContext(Dispatchers.Main) {
                    _uiState.value = ScanUiState.Error(error.message ?: "Unknown Error")
                }
            })
        }
    }

    suspend fun setLoading() =
        withContext(Dispatchers.Main) { _uiState.value = ScanUiState.Loading }

    suspend fun setSuccess(data: ScanResponse) = withContext(Dispatchers.Main) {
        _uiState.value = ScanUiState.Success(data)
    }
}


