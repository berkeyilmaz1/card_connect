package com.berkeyilmaz.cardapp.domain.contact.usecase

import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import javax.inject.Inject

class GetContactByIdUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(contactId: String) = repository.getContactById(contactId)
}
