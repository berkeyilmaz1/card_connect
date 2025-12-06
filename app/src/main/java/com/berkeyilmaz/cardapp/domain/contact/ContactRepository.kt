package com.berkeyilmaz.cardapp.domain.contact

import android.content.ContentResolver
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact

interface ContactRepository {
    suspend fun getInternalContacts(contentResolver: ContentResolver): List<InternalContact>
    suspend fun getRemoteContacts(): Result<List<Contact>>
}