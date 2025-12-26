package com.berkeyilmaz.cardapp.presentation.main.scan.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.domain.scan.usecase.ScanUseCase
import com.berkeyilmaz.cardapp.domain.scan.usecase.ScanImageOnDeviceUseCase
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

sealed class ScanUiState {
    object Idle : ScanUiState()
    object Loading : ScanUiState()
    data class Success(val data: ScanResponse) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanUseCase: ScanUseCase,
    private val scanImageOnDeviceUseCase: ScanImageOnDeviceUseCase
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
            val startTime = System.currentTimeMillis()
            setLoading()

            // Orijinal boyut
            val originalSizeKb = file.length() / 1024
            Log.d("BerkeTAG", "Original image size: ${originalSizeKb}KB")

            // Görüntüyü sıkıştır
            val compressedFile = withContext(Dispatchers.IO) {
                compressImage(file, maxSizeKb = 800)
            }

            val compressedSizeKb = compressedFile.length() / 1024
            Log.d("BerkeTAG", "Compressed image size: ${compressedSizeKb}KB")

            val result = scanUseCase(compressedFile)
            Log.i("BerkeTAG", "Scan result: $result")

            result.fold(onSuccess = { data ->
                val updatedData = data.copy(imageUrl = compressedFile.absolutePath)
                val endTime = System.currentTimeMillis()
                val duration = endTime - startTime
                Log.d("BerkeTAGTIME", "Scan completed in ${duration}ms")
                setSuccess(updatedData)
            }, onFailure = { error ->
                withContext(Dispatchers.Main) {
                    _uiState.value = ScanUiState.Error(error.message ?: "Unknown Error")
                }
            })
        }
    }

    fun compressImage(
        file: File, maxSizeKb: Int
    ): File {

        val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return file

        var quality = 90
        var compressedFile: File

        do {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)

            compressedFile = File.createTempFile(
                "compressed_", ".jpg", file.parentFile
            )

            FileOutputStream(compressedFile).use {
                it.write(stream.toByteArray())
            }

            quality -= 10

        } while (compressedFile.length() / 1024 > maxSizeKb && quality > 30)

        return compressedFile
    }

    fun scanImageOnDevice(file: File) {
        viewModelScope.launch {
            setLoading()
            val result = scanImageOnDeviceUseCase(file)
            Log.i("BerkeTAG", "On-device scan result: $result")
            result.fold(onSuccess = { data ->
                setSuccess(data)
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


