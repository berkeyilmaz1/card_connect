package com.berkeyilmaz.cardapp.domain.contact.usecase

import android.content.ContentResolver
import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import javax.inject.Inject

class MergeContactsUseCase @Inject constructor(private val repository: ContactRepository) {
    suspend operator fun invoke(
        contentResolver: ContentResolver,
        primary: Contact,
        duplicates: List<Contact>,
        internalDuplicates: List<InternalContact>
    ) = repository.mergeContacts(contentResolver, primary, duplicates, internalDuplicates)
}
