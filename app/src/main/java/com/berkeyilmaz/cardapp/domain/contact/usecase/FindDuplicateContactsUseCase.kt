package com.berkeyilmaz.cardapp.domain.contact.usecase

import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import javax.inject.Inject

class FindDuplicateContactsUseCase @Inject constructor(private val repository: ContactRepository) {
    suspend operator fun invoke(
        remoteContacts: List<Contact>,
        internalContacts: List<InternalContact>
    ) = repository.findDuplicateContacts(remoteContacts, internalContacts)
}
