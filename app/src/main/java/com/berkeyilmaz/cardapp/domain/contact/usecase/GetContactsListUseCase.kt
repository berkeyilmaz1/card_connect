package com.berkeyilmaz.cardapp.domain.contact.usecase

import android.content.ContentResolver
import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import javax.inject.Inject

class GetContactsListUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(contentResolver: ContentResolver) =
        repository.getContacts(contentResolver)
}