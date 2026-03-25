package com.berkeyilmaz.cardapp.domain.contact

import android.content.ContentResolver
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.contact.model.DuplicateContactGroup
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContactChanges
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest

interface ContactRepository {
    suspend fun getInternalContacts(contentResolver: ContentResolver): List<InternalContact>
    suspend fun getInternalContactsWithChanges(contentResolver: ContentResolver): Pair<List<InternalContact>, InternalContactChanges>
    suspend fun getRemoteContacts(): Result<List<Contact>>
    suspend fun createContact(contactRequest: ContactRequest): Result<Contact>
    suspend fun searchContactThatUserAsked(text: String, contacts: List<Contact>): List<Contact>
    suspend fun suggestTagsForNewContact(internalContactList: List<InternalContact>): List<ContactRequest>
    suspend fun findDuplicateContacts(
        remoteContacts: List<Contact>,
        internalContacts: List<InternalContact>
    ): List<DuplicateContactGroup>
    suspend fun mergeContacts(
        contentResolver: ContentResolver,
        primaryContact: Contact,
        duplicates: List<Contact>,
        internalDuplicates: List<InternalContact>
    ): Result<Contact>
    suspend fun syncInternalContactsToFirestore(contacts: List<InternalContact>): Result<Unit>
}