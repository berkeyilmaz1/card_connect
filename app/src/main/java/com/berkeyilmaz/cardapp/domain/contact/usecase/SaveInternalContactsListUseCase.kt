package com.berkeyilmaz.cardapp.domain.contact.usecase

import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import javax.inject.Inject

class SaveInternalContactsListUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(userId: String, internalContacts: List<InternalContact>) =
        repository.saveInternalContactList(userId, internalContacts)
}