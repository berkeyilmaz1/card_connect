package com.berkeyilmaz.cardapp.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import com.berkeyilmaz.cardapp.core.manager.GeminiExtractor
import com.berkeyilmaz.cardapp.domain.scan.ScanRepository
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import com.berkeyilmaz.cardapp.domain.settings.usecase.GetUseLocalLlmUseCase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

class ScanRepositoryImpl @Inject constructor(
    private val textRecognizer: TextRecognizer,
    @param:ApplicationContext private val context: Context,
    private val getUseLocalLlmUseCase: GetUseLocalLlmUseCase,
) : ScanRepository {

//    override suspend fun scanImage(image: MultipartBody.Part): Result<ScanResponse> {
//        return try {
//            val authToken = getAuthToken()
//            val response = scanService.scanImage(image, authToken)
//            if (response.isSuccessful && response.body() != null) {
//                Result.success(response.body()!!)
//            } else {
//                Result.failure(Exception("Scan failed with code: ${response.code()}"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

    override suspend fun scanImageOnDevice(file: File): Result<ScanResponse> {
        return try {
            val startTime = System.currentTimeMillis()
            val inputImage = InputImage.fromFilePath(context, Uri.fromFile(file))
            val result = textRecognizer.process(inputImage).await()

            val recognizedText = result.text
            Log.i("BerkeTAG", "Recognized text: $recognizedText")

            val useLocalLlm = getUseLocalLlmUseCase().first()
            Log.i("ScanRepositoryImpl", "Using Local LLM: $useLocalLlm")

            val llmTime = System.currentTimeMillis()
            val scanResponse = if (useLocalLlm) {
                // TODO: Implement local LLM call
                Log.w("ScanRepositoryImpl", "Local LLM not implemented yet, falling back to Gemini")
                GeminiExtractor.extractFromText(recognizedText)
            } else {
                GeminiExtractor.extractFromText(recognizedText)
            }

            val endTime = System.currentTimeMillis()
            Log.i(
                "BerkeTIME",
                "Total time: ${endTime - startTime}) ms , OCR time: ${llmTime - startTime} ms, LLM time: ${endTime - llmTime} ms"
            )
            scanResponse.imageUrl = file.absolutePath
            scanResponse.copy(llmSource = if (useLocalLlm) "Local LLM" else "Gemini LLM")

            Result.success(scanResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}