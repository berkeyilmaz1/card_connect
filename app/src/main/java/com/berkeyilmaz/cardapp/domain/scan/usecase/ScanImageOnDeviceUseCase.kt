package com.berkeyilmaz.cardapp.domain.scan.usecase

import com.berkeyilmaz.cardapp.domain.scan.ScanRepository
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import java.io.File
import javax.inject.Inject

class ScanImageOnDeviceUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    suspend operator fun invoke(file: File): Result<ScanResponse> {
        return repository.scanImageOnDevice(file)
    }
}