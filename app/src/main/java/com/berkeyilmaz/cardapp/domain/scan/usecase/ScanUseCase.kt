package com.berkeyilmaz.cardapp.domain.scan.usecase

import com.berkeyilmaz.cardapp.domain.scan.ScanRepository
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

//class ScanUseCase @Inject constructor(
//    private val repository: ScanRepository
//) {
//    suspend operator fun invoke(file: File): Result<ScanResponse> {
//        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
//        return repository.scanImage(body)
//    }
//}