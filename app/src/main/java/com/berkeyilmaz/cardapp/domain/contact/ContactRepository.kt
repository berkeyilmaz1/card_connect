package com.berkeyilmaz.cardapp.domain.contact

import android.content.ContentResolver
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest

interface ContactRepository {
    suspend fun getInternalContacts(contentResolver: ContentResolver): List<InternalContact>
    suspend fun getRemoteContacts(): Result<List<Contact>>
    suspend fun createContact(contactRequest: ContactRequest): Result<Contact>
    suspend fun searchContactThatUserAsked(text: String, contacts: List<Contact>): List<Contact>
}