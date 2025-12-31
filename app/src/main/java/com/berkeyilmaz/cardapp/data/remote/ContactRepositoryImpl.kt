package com.berkeyilmaz.cardapp.data.remote

import android.content.ContentResolver
import android.provider.ContactsContract
import android.util.Log
import com.berkeyilmaz.cardapp.core.manager.GeminiExtractor
import com.berkeyilmaz.cardapp.data.local.dao.InternalContactDAO
import com.berkeyilmaz.cardapp.data.local.mapper.toDomainList
import com.berkeyilmaz.cardapp.data.local.mapper.toEntityList
import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContactChanges
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val internalContactDao: InternalContactDAO,
) : ContactRepository {

    override suspend fun getInternalContacts(contentResolver: ContentResolver): List<InternalContact> =
        withContext(Dispatchers.IO) {
            fetchContactsFromDevice(contentResolver)
        }

    private fun fetchContactsFromDevice(contentResolver: ContentResolver): List<InternalContact> {
        val contacts = mutableListOf<InternalContact>()

        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
        )

        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
        )

        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val hasPhoneNumberIndex =
                it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)

            while (it.moveToNext()) {
                val id = it.getString(idIndex)
                val name = it.getString(nameIndex)
                val hasPhoneNumber = it.getInt(hasPhoneNumberIndex) > 0
                if (name.isNullOrEmpty()) continue

                // Phone numbers
                val phoneNumbers = mutableListOf<String>()
                if (hasPhoneNumber) {
                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(id),
                        null
                    )

                    phoneCursor?.use { pc ->
                        val numberIndex =
                            pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        while (pc.moveToNext()) {
                            val number = pc.getString(numberIndex)
                            phoneNumbers.add(number)
                        }
                    }
                }

                // Emails
                val emails = mutableListOf<String>()
                val emailCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
                    "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
                    arrayOf(id),
                    null
                )
                emailCursor?.use { ec ->
                    val emailIndex =
                        ec.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
                    while (ec.moveToNext()) {
                        val email = ec.getString(emailIndex)
                        if (!email.isNullOrEmpty()) {
                            emails.add(email)
                        }
                    }
                }

                // Websites
                val websites = mutableListOf<String>()
                val websiteCursor = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Website.URL),
                    "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(id, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE),
                    null
                )
                websiteCursor?.use { wc ->
                    val urlIndex =
                        wc.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)
                    while (wc.moveToNext()) {
                        val url = wc.getString(urlIndex)
                        if (!url.isNullOrEmpty()) {
                            websites.add(url)
                        }
                    }
                }

                // Organization and Title
                var organization: String? = null
                var title: String? = null
                val orgCursor = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Organization.COMPANY,
                        ContactsContract.CommonDataKinds.Organization.TITLE
                    ),
                    "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(
                        id,
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
                    ),
                    null
                )
                orgCursor?.use { oc ->
                    if (oc.moveToFirst()) {
                        val companyIndex =
                            oc.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY)
                        val titleIndex =
                            oc.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE)
                        organization = oc.getString(companyIndex)
                        title = oc.getString(titleIndex)
                    }
                }

                // Address
                var address: String? = null
                val addressCursor = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS),
                    "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(
                        id,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
                    ),
                    null
                )
                addressCursor?.use { ac ->
                    if (ac.moveToFirst()) {
                        val addressIndex =
                            ac.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)
                        address = ac.getString(addressIndex)
                    }
                }

                // Note
                var note: String? = null
                val noteCursor = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Note.NOTE),
                    "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(id, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE),
                    null
                )
                noteCursor?.use { nc ->
                    if (nc.moveToFirst()) {
                        val noteIndex =
                            nc.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE)
                        note = nc.getString(noteIndex)
                    }
                }

                contacts.add(
                    InternalContact(
                        contactId = id,
                        fullName = name,
                        phoneNumbers = phoneNumbers,
                        emails = emails.ifEmpty { null },
                        websites = websites.ifEmpty { null },
                        organization = organization,
                        title = title,
                        address = address,
                        note = note
                    )
                )
            }
        }
        return contacts
    }

    override suspend fun getInternalContactsWithChanges(contentResolver: ContentResolver): Pair<List<InternalContact>, InternalContactChanges> =
        withContext(Dispatchers.IO) {
            try {
                // Get current contacts from device
                val currentContacts = fetchContactsFromDevice(contentResolver)
                Log.d(
                    "BerkeTag",
                    "ContactRepositoryImpl - fetched ${currentContacts.size} contacts from device"
                )

                // Get saved contacts from database
                val savedContacts = internalContactDao.getAllContacts().toDomainList()
                Log.d(
                    "BerkeTag",
                    "ContactRepositoryImpl - fetched ${savedContacts.size} contacts from database"
                )

                // Create maps for comparison
                val currentMap = currentContacts.associateBy { it.contactId }
                val savedMap = savedContacts.associateBy { it.contactId }

                // Find added contacts (in current but not in saved)
                val added = currentContacts.filter { it.contactId !in savedMap.keys }

                // Find removed contacts (in saved but not in current)
                val removed = savedContacts.filter { it.contactId !in currentMap.keys }

                // Find modified contacts (in both but different)
                val modified = currentContacts.filter { current ->
                    val saved = savedMap[current.contactId]
                    saved != null && saved != current
                }

                // Save current contacts to database (replace all)
                internalContactDao.replaceAllContacts(currentContacts.toEntityList())

                val changes = InternalContactChanges(
                    added = added,
                    removed = removed,
                    modified = modified
                )
                Log.d(
                    "BerkeTag",
                    "ContactRepositoryImpl- currentContacts: ${currentContacts.size}, added: ${added.size}, removed: ${removed.size}, modified: ${modified.size}"
                )

                Pair(currentContacts, changes)
            } catch (e: Exception) {
                Log.e(
                    "BerkeTag",
                    "ContactRepositoryImpl - Error in getInternalContactsWithChanges: ${e.message}",
                    e
                )
                throw e
            }
        }

    override suspend fun getRemoteContacts(): Result<List<Contact>> {
        return try {
            val currentUser = firebaseAuth.currentUser
                ?: return Result.failure(Exception("User not authenticated"))
            Log.i("BerkeTag", "Fetching contacts for user: ${currentUser.uid}")
            val documentRef = database.collection("users")
                .document(currentUser.uid)
                .collection("contacts")
                .get().await()
            Log.i("BerkeTag", "Fetched ${documentRef.size()} contacts from Firestore")

            val contacts = documentRef.documents.mapNotNull { documentSnapshot ->
                val contact = documentSnapshot.toObject<Contact>()
                contact?.copy(contactId = documentSnapshot.id)
            }
            Log.i("BerkeTag", "Group Contacts: $contacts contacts from documents")
            Result.success(contacts)
        } catch (e: Exception) {
            Log.e("BerkeTag", "Error fetching contacts: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun searchContactThatUserAsked(
        text: String, contacts: List<Contact>
    ): List<Contact> {
        return try {
            Log.i("ContactRepositoryImpl", "contacts to search: $contacts")
            val response = GeminiExtractor.findContactThatUserAsked(text, contacts)
            Log.i("ContactRepositoryImpl", "Search response for '$text': $response")
            response
        } catch (e: Exception) {
            Log.e("ContactRepositoryImpl", "Search exception for '$text': ${e.message}")
            emptyList()
        }
    }

    override suspend fun suggestTagsForNewContact(internalContactList: List<InternalContact>): List<ContactRequest> {
        return try {
            val response = GeminiExtractor.suggestTagsForNewContact(internalContactList)
            response
        } catch (e: Exception) {
            Log.e("ContactRepositoryImpl", "Tag suggestion exception: ${e.message}")
            emptyList()
        }
    }

    override suspend fun createContact(contactRequest: ContactRequest): Result<Contact> {
        return try {
            val currentUser = firebaseAuth.currentUser
                ?: return Result.failure(Exception("User not authenticated"))

            val documentRef = database.collection("users")
                .document(currentUser.uid)
                .collection("contacts")
                .add(contactRequest)
                .await()

            val contact = Contact(
                contactId = documentRef.id,
                fullName = contactRequest.fullName,
                organization = contactRequest.organization,
                title = contactRequest.title,
                emails = contactRequest.emails,
                phones = contactRequest.phones,
                tags = contactRequest.tags

            )

            Result.success(contact)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}