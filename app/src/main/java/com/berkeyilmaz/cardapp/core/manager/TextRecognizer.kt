package com.berkeyilmaz.cardapp.core.manager

import android.content.Context
import android.net.Uri
import com.berkeyilmaz.cardapp.data.model.ApiResult
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

object TextRecognizerManager {

    // Latin text recognizer
    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }


    suspend fun recognizeTextFromUri(context: Context, uri: Uri): ApiResult<Text?> {
        return try {
            val image = InputImage.fromFilePath(context, uri)
            val text = recognizer.process(image).await()
            ApiResult.Success(text)
        } catch (e: Exception) {
            return ApiResult.Error(e)
        }
    }
}