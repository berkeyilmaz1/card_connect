package com.berkeyilmaz.cardapp.domain.scan

import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import com.google.mlkit.vision.common.InputImage
import okhttp3.MultipartBody
import java.io.File

interface ScanRepository {
    suspend fun scanImage(image: MultipartBody.Part): Result<ScanResponse>
    suspend fun scanImageOnDevice(file: File): Result<ScanResponse>
    suspend fun createContact(contactRequest: ContactRequest): Result<Unit>
}