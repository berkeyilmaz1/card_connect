package com.berkeyilmaz.cardapp.domain.contact.usecase

import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import javax.inject.Inject

class SyncInternalContactsUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(contacts: List<InternalContact>): Result<Unit> =
        repository.syncInternalContactsToFirestore(contacts)
}
