package com.berkeyilmaz.cardapp.data.remote

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.berkeyilmaz.cardapp.core.manager.GeminiExtractor
import com.berkeyilmaz.cardapp.core.manager.LocalLlmExtractor
import com.berkeyilmaz.cardapp.core.util.ContactsHelper
import com.berkeyilmaz.cardapp.data.local.dao.InternalContactDAO
import com.berkeyilmaz.cardapp.data.local.mapper.toDomainList
import com.berkeyilmaz.cardapp.data.local.mapper.toEntityList
import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.contact.model.DuplicateContactGroup
import com.berkeyilmaz.cardapp.domain.contact.model.DuplicateMatchReason
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContactChanges
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import com.berkeyilmaz.cardapp.domain.settings.usecase.GetUseLocalLlmUseCase
import com.berkeyilmaz.cardapp.core.util.recordNonFatal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val internalContactDao: InternalContactDAO,
    private val getUseLocalLlmUseCase: GetUseLocalLlmUseCase,
    private val localLlmExtractor: LocalLlmExtractor,
    private val crashlytics: FirebaseCrashlytics,
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
            val hasPhoneNumberIndex = it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)

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
                    val urlIndex = wc.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)
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
                        id, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
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
                        id, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
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
                val savedContacts = internalContactDao.getAllContacts()
                    .toDomainList()
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
                    added = added, removed = removed, modified = modified
                )
                Log.d(
                    "BerkeTag",
                    "ContactRepositoryImpl- currentContacts: ${currentContacts.size}, added: ${added.size}, removed: ${removed.size}, modified: ${modified.size}"
                )

                Pair(currentContacts, changes)
            } catch (e: Exception) {
                crashlytics.recordNonFatal(e)
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
                .get()
                .await()
            Log.i("BerkeTag", "Fetched ${documentRef.size()} contacts from Firestore")

            val contacts = documentRef.documents.mapNotNull { documentSnapshot ->
                val contact = documentSnapshot.toObject<Contact>()
                contact?.copy(contactId = documentSnapshot.id)
            }
            Log.i("BerkeTag", "Group Contacts: $contacts contacts from documents")
            Result.success(contacts)
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            Log.e("BerkeTag", "Error fetching contacts: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun searchContactThatUserAsked(
        text: String, contacts: List<Contact>
    ): List<Contact> {
        return try {
            val useLocalLlm = getUseLocalLlmUseCase().first()
            val isModelReady = localLlmExtractor.isModelReady()
            Log.i("ContactRepositoryImpl", "contacts to search: $contacts")
            Log.i(
                "ContactRepositoryImpl", "Using Local LLM: $useLocalLlm, Model Ready: $isModelReady"
            )

            val response = if (useLocalLlm && isModelReady) {
                Log.i("ContactRepositoryImpl", "Using Local LLM for contact search")
                val localResult = localLlmExtractor.findContactThatUserAsked(text, contacts)
                localResult.ifEmpty {
                    Log.w(
                        "ContactRepositoryImpl", "Local LLM returned empty, falling back to Gemini"
                    )
                    GeminiExtractor.findContactThatUserAsked(text, contacts)
                }
            } else {
                if (useLocalLlm) {
                    Log.w(
                        "ContactRepositoryImpl",
                        "Local LLM enabled but model not ready, using Gemini"
                    )
                }
                GeminiExtractor.findContactThatUserAsked(text, contacts)
            }
            Log.i("ContactRepositoryImpl", "Search response for '$text': $response")
            response
        } catch (e: Exception) {
            Log.e("ContactRepositoryImpl", "Search exception for '$text': ${e.message}")
            emptyList()
        }
    }

    override suspend fun suggestTagsForNewContact(internalContactList: List<InternalContact>): List<ContactRequest> {
        return try {
            val useLocalLlm = getUseLocalLlmUseCase().first()
            val isModelReady = localLlmExtractor.isModelReady()
            Log.i(
                "ContactRepositoryImpl", "Suggesting tags for ${internalContactList.size} contacts"
            )
            Log.i(
                "ContactRepositoryImpl", "Using Local LLM: $useLocalLlm, Model Ready: $isModelReady"
            )

            val response = if (useLocalLlm && isModelReady) {
                Log.i("ContactRepositoryImpl", "Using Local LLM for tag suggestion")
                val localResult = localLlmExtractor.suggestTagsForNewContact(internalContactList)
                if (localResult.isNotEmpty()) {
                    localResult.map { it.copy(llmSource = "Local LLM") }
                } else {
                    Log.w(
                        "ContactRepositoryImpl", "Local LLM returned empty, falling back to Gemini"
                    )
                    GeminiExtractor.suggestTagsForNewContact(internalContactList)
                        .map { it.copy(llmSource = "Gemini LLM") }
                }
            } else {
                if (useLocalLlm) {
                    Log.w(
                        "ContactRepositoryImpl",
                        "Local LLM enabled but model not ready, using Gemini"
                    )
                }
                GeminiExtractor.suggestTagsForNewContact(internalContactList)
                    .map { it.copy(llmSource = "Gemini LLM") }
            }

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
                internalContactId = contactRequest.internalContactId,
                fullName = contactRequest.fullName,
                organization = contactRequest.organization,
                title = contactRequest.title,
                emails = contactRequest.emails,
                phones = contactRequest.phones,
                tags = contactRequest.tags
            )

            Result.success(contact)
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            Result.failure(e)
        }
    }

    private fun normalizePhone(phone: String): String {
        val d = phone.filter { it.isDigit() }
        return when {
            d.startsWith("90") && d.length == 12 -> d
            d.startsWith("0") && d.length == 11 -> "9$d"
            d.length == 10 -> "90$d"
            else -> d
        }
    }

    private fun normalizeEmail(email: String) = email.trim()
        .lowercase()

    override suspend fun findDuplicateContacts(
        remoteContacts: List<Contact>, internalContacts: List<InternalContact>
    ): List<DuplicateContactGroup> = withContext(Dispatchers.IO) {
        // normalized phone -> list of remote contacts
        val phoneMap = mutableMapOf<String, MutableList<Contact>>()
        // normalized email -> list of remote contacts
        val emailMap = mutableMapOf<String, MutableList<Contact>>()

        for (contact in remoteContacts) {
            for (phone in contact.phones) {
                val normalized = normalizePhone(phone)
                if (normalized.isNotEmpty()) {
                    phoneMap.getOrPut(normalized) { mutableListOf() }
                        .add(contact)
                }
            }
            for (email in contact.emails) {
                val normalized = normalizeEmail(email)
                if (normalized.isNotEmpty()) {
                    emailMap.getOrPut(normalized) { mutableListOf() }
                        .add(contact)
                }
            }
        }

        // normalized phone -> list of internal contacts
        val internalPhoneMap = mutableMapOf<String, MutableList<InternalContact>>()
        // normalized email -> list of internal contacts
        val internalEmailMap = mutableMapOf<String, MutableList<InternalContact>>()

        for (internal in internalContacts) {
            for (phone in internal.phoneNumbers) {
                val normalized = normalizePhone(phone)
                if (normalized.isNotEmpty()) {
                    internalPhoneMap.getOrPut(normalized) { mutableListOf() }
                        .add(internal)
                }
            }
            for (email in internal.emails ?: emptyList()) {
                val normalized = normalizeEmail(email)
                if (normalized.isNotEmpty()) {
                    internalEmailMap.getOrPut(normalized) { mutableListOf() }
                        .add(internal)
                }
            }
        }

        val result = mutableListOf<DuplicateContactGroup>()
        // Track which (remoteContactId sets + internalContactId sets) combos already added
        val processedKeys = mutableSetOf<Pair<Set<String>, Set<String>>>()

        // Collect all normalized keys that appear in at least one map on both sides OR in 2+ remotes
        val allPhoneKeys = (phoneMap.keys + internalPhoneMap.keys).toSet()
        val allEmailKeys = (emailMap.keys + internalEmailMap.keys).toSet()

        // Firebase contact'lardan internalContactId'si olan → zaten uygulamadan kaydedilmiş,
        // telefon rehberindeki karşılığı bilinen kişiler. Bunların internal ID'lerini set'e al.
        val linkedInternalIds = remoteContacts.filter { it.internalContactId.isNotEmpty() }
            .map { it.internalContactId }
            .toSet()

        // Phone duplicate groups
        for (phone in allPhoneKeys) {
            val remotes = phoneMap[phone] ?: emptyList()
            // Uygulamadan kaydedildiği bilinen internal'ları çıkar
            val internals = (internalPhoneMap[phone]
                ?: emptyList()).filter { it.contactId !in linkedInternalIds }

            if (remotes.size < 2 && internals.size < 2) continue

            val remoteIds = remotes.map { it.contactId }
                .toSet()
            val internalIds = internals.map { it.contactId }
                .toSet()
            val key = remoteIds to internalIds
            if (key in processedKeys) continue

            val matchingEmailKey = allEmailKeys.firstOrNull { email ->
                val er = (emailMap[email] ?: emptyList()).map { it.contactId }
                    .toSet()
                val ei = (internalEmailMap[email]
                    ?: emptyList()).filter { it.contactId !in linkedInternalIds }
                    .map { it.contactId }
                    .toSet()
                er == remoteIds && ei == internalIds
            }

            result.add(
                DuplicateContactGroup(
                    contacts = remotes,
                    internalContacts = internals,
                    matchReason = if (matchingEmailKey != null) DuplicateMatchReason.PHONE_AND_EMAIL
                    else DuplicateMatchReason.PHONE,
                    sharedValue = if (matchingEmailKey != null) "$phone / $matchingEmailKey" else phone
                )
            )
            processedKeys.add(key)
        }

        // Email duplicate groups (only those not already covered by phone pass)
        for (email in allEmailKeys) {
            val remotes = emailMap[email] ?: emptyList()
            val internals = (internalEmailMap[email]
                ?: emptyList()).filter { it.contactId !in linkedInternalIds }
            if (remotes.size < 2 && internals.size < 2) continue

            val remoteIds = remotes.map { it.contactId }
                .toSet()
            val internalIds = internals.map { it.contactId }
                .toSet()
            val key = remoteIds to internalIds
            if (key in processedKeys) continue

            result.add(
                DuplicateContactGroup(
                    contacts = remotes,
                    internalContacts = internals,
                    matchReason = DuplicateMatchReason.EMAIL,
                    sharedValue = email
                )
            )
            processedKeys.add(key)
        }

        Log.d(
            "BerkeTag",
            "findDuplicateContacts: found ${result.size} duplicate groups (remote+internal)"
        )
        result
    }

    override suspend fun mergeContacts(
        contentResolver: ContentResolver,
        primaryContact: Contact,
        duplicates: List<Contact>,
        internalDuplicates: List<InternalContact>
    ): Result<Contact> {
        return try {
            val currentUser = firebaseAuth.currentUser
                ?: return Result.failure(Exception("User not authenticated"))

            val allRemote = listOf(primaryContact) + duplicates
            val allInternal = internalDuplicates

            // --- Merge alanları ---
            val mergedPhones =
                (allRemote.flatMap { it.phones } + allInternal.flatMap { it.phoneNumbers }).map {
                    normalizePhone(it)
                }
                    .filter { it.isNotEmpty() }
                    .distinct()
            val mergedEmails = (allRemote.flatMap { it.emails } + allInternal.flatMap {
                it.emails ?: emptyList()
            }).map { normalizeEmail(it) }
                .filter { it.isNotEmpty() }
                .distinct()
            val mergedTags = allRemote.flatMap { it.tags }
                .distinctBy { it.name }
            val mergedWebsites = (allRemote.flatMap { it.websites } + allInternal.flatMap {
                it.websites ?: emptyList()
            }).distinct()
            val mergedSocialMedias = allRemote.flatMap { it.socialMedias }
                .distinctBy { it.url }

            val fullName = primaryContact.fullName.ifEmpty {
                allRemote.drop(1)
                    .firstOrNull { it.fullName.isNotEmpty() }?.fullName
                    ?: allInternal.firstOrNull { it.fullName.isNotEmpty() }?.fullName ?: ""
            }
            val title = primaryContact.title.ifEmpty {
                allRemote.drop(1)
                    .firstOrNull { it.title.isNotEmpty() }?.title
                    ?: allInternal.firstOrNull { !it.title.isNullOrEmpty() }?.title ?: ""
            }
            val organization = primaryContact.organization.ifEmpty {
                allRemote.drop(1)
                    .firstOrNull { it.organization.isNotEmpty() }?.organization
                    ?: allInternal.firstOrNull { !it.organization.isNullOrEmpty() }?.organization
                    ?: ""
            }
            val note = primaryContact.note.ifEmpty {
                allRemote.drop(1)
                    .firstOrNull { it.note.isNotEmpty() }?.note
                    ?: allInternal.firstOrNull { !it.note.isNullOrEmpty() }?.note ?: ""
            }
            val address = primaryContact.address.ifEmpty {
                allRemote.drop(1)
                    .firstOrNull { it.address.isNotEmpty() }?.address
                    ?: allInternal.firstOrNull { !it.address.isNullOrEmpty() }?.address ?: ""
            }
            val imageUrl = primaryContact.imageUrl.ifEmpty {
                allRemote.drop(1)
                    .firstOrNull { it.imageUrl.isNotEmpty() }?.imageUrl ?: ""
            }

            val mergedContact = primaryContact.copy(
                fullName = fullName,
                title = title,
                organization = organization,
                phones = mergedPhones,
                emails = mergedEmails,
                tags = mergedTags,
                note = note,
                address = address,
                imageUrl = imageUrl,
                socialMedias = mergedSocialMedias,
                websites = mergedWebsites
            )

            // --- Firebase: primary'yi güncelle, duplicate'leri sil ---
            val batch = database.batch()
            val contactsCollection = database.collection("users")
                .document(currentUser.uid)
                .collection("contacts")
            batch.set(contactsCollection.document(primaryContact.contactId), mergedContact)
            for (duplicate in duplicates) {
                batch.delete(contactsCollection.document(duplicate.contactId))
            }
            batch.commit()
                .await()
            Log.d(
                "BerkeTag",
                "mergeContacts: Firebase batch done — updated primary, deleted ${duplicates.size} duplicates"
            )

            // --- Local rehber: tüm internal duplicate'leri sil ---
            if (internalDuplicates.isNotEmpty()) {
                deleteInternalContacts(contentResolver, internalDuplicates)
            }

            // --- Local rehber: merged contact'ı ekle ---
            ContactsHelper.addContactToPhone(
                context = context,
                displayName = mergedContact.fullName,
                phoneNumber = mergedContact.phones.firstOrNull(),
                email = mergedContact.emails.firstOrNull(),
                company = mergedContact.organization.ifEmpty { null },
                jobTitle = mergedContact.title.ifEmpty { null },
                address = mergedContact.address.ifEmpty { null },
                website = mergedContact.websites.firstOrNull(),
                notes = mergedContact.note.ifEmpty { null })
            Log.d(
                "BerkeTag", "mergeContacts: local contact created for '${mergedContact.fullName}'"
            )

            Result.success(mergedContact)
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            Log.e("BerkeTag", "mergeContacts error: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun saveInternalContactList(
        userId: String, internalContacts: List<InternalContact>
    ): ResponseState<Boolean> {
        return try {
            val currentUser =
                firebaseAuth.currentUser ?: return ResponseState.Error("User not authenticated")

            val ref = database.document("users")
                .collection(userId)
                .document("contacts")
                .set(internalContacts)
                .await()

            Log.d(
                "BerkeTag",
                "saveInternalContactList: saved ${internalContacts.size} contacts for user $userId"
            )
            ResponseState.Success(true)
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            Log.e("BerkeTag", "saveInternalContactList error: ${e.message}", e)
            return ResponseState.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun deleteInternalContacts(
        contentResolver: ContentResolver, contacts: List<InternalContact>
    ) {
        if (contacts.isEmpty()) return

        val ops = arrayListOf<ContentProviderOperation>()

        for (contact in contacts) {
            // Contacts._ID → RawContacts üzerinden bul, tek tek sil
            val rawCursor = contentResolver.query(
                ContactsContract.RawContacts.CONTENT_URI,
                arrayOf(ContactsContract.RawContacts._ID),
                "${ContactsContract.RawContacts.CONTACT_ID} = ?",
                arrayOf(contact.contactId),
                null
            )
            rawCursor?.use { cursor ->
                val idIndex = cursor.getColumnIndex(ContactsContract.RawContacts._ID)
                while (cursor.moveToNext()) {
                    val rawId = cursor.getString(idIndex)
                    ops.add(
                        ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                            .withSelection(
                                "${ContactsContract.RawContacts._ID} = ?", arrayOf(rawId)
                            )
                            .build()
                    )
                }
            }
        }

        if (ops.isNotEmpty()) {
            try {
                contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
                Log.d(
                    "BerkeTag",
                    "deleteInternalContacts: deleted ${contacts.size} contacts (${ops.size} raw rows)"
                )
            } catch (e: Exception) {
                crashlytics.recordNonFatal(e)
                Log.e("BerkeTag", "deleteInternalContacts error: ${e.message}", e)
            }
        }

        // Room cache'ten de kaldır
        val ids = contacts.map { it.contactId }
        internalContactDao.deleteByIds(ids)
        Log.d("BerkeTag", "deleteInternalContacts: removed ${ids.size} entries from Room cache")
    }

}