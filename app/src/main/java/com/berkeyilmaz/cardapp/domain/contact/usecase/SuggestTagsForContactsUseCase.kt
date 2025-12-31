package com.berkeyilmaz.cardapp.domain.contact.usecase

import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import javax.inject.Inject

class SuggestTagsForContactsUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(contacts: List<InternalContact>): List<ContactRequest> {
        return repository.suggestTagsForNewContact(contacts)
    }
}

