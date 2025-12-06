package com.berkeyilmaz.cardapp.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import com.berkeyilmaz.cardapp.core.manager.GeminiExtractor
import com.berkeyilmaz.cardapp.data.remote.service.ScanService
import com.berkeyilmaz.cardapp.domain.scan.ScanRepository
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject

class ScanRepositoryImpl @Inject constructor(
    private val scanService: ScanService,
    private val firebaseAuth: FirebaseAuth,
    private val textRecognizer: TextRecognizer,
    @param:ApplicationContext private val context: Context
) : ScanRepository {

    private suspend fun getAuthToken(): String {
        return try {
            val token = firebaseAuth.currentUser?.getIdToken(false)?.await()?.token
            "Bearer ${token ?: ""}"
        } catch (e: Exception) {
            "Bearer "
        }
    }

    override suspend fun scanImage(image: MultipartBody.Part): Result<ScanResponse> {
        return try {
            val authToken = getAuthToken()
            val response = scanService.scanImage(image, authToken)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Scan failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun scanImageOnDevice(file: File): Result<ScanResponse> {
        return try {
            val startTime = System.currentTimeMillis()
            val inputImage = InputImage.fromFilePath(context, Uri.fromFile(file))
            val result = textRecognizer.process(inputImage).await()

            val recognizedText = result.text
            Log.i("BerkeTAG", "Recognized text: $recognizedText")
            val scanResponse = GeminiExtractor.extractFromText(recognizedText)
            val endTime = System.currentTimeMillis()
            Log.i("BerkeTIME", "Text recognition and extraction took ${endTime - startTime} ms")

            scanResponse.imageUrl = file.absolutePath
            scanResponse.rawText = recognizedText

            Result.success(scanResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createContact(contactRequest: ContactRequest): Result<Unit> {
        return try {
            val authToken = getAuthToken()
            Log.i("BerkeTAG", "Creating contact with request: ${Gson().toJson(contactRequest)}")
            val response = scanService.createContact(contactRequest, authToken)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Create contact failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}