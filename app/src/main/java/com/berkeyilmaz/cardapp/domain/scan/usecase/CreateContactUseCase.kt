package com.berkeyilmaz.cardapp.domain.scan.usecase

import com.berkeyilmaz.cardapp.domain.scan.ScanRepository
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import javax.inject.Inject

class CreateContactUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    suspend operator fun invoke(contactRequest: ContactRequest): Result<Unit> {
        return repository.createContact(contactRequest)
    }
}