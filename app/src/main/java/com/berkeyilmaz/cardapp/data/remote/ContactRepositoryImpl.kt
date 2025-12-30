package com.berkeyilmaz.cardapp.data.remote

import android.content.ContentResolver
import android.provider.ContactsContract
import android.util.Log
import com.berkeyilmaz.cardapp.core.manager.GeminiExtractor
import com.berkeyilmaz.cardapp.data.remote.service.ScanService
import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val scanService: ScanService,
    private val database: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : ContactRepository {
    private suspend fun getAuthToken(): String {
        return try {
            val token = firebaseAuth.currentUser?.getIdToken(false)?.await()?.token
            "Bearer ${token ?: ""}"
        } catch (e: Exception) {
            "Bearer "
        }
    }

    override suspend fun getInternalContacts(contentResolver: ContentResolver): List<InternalContact> =
        withContext(
            Dispatchers.IO
        ) {
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
                    contacts.add(
                        InternalContact(
                            internalId = id, fullName = name, phoneNumbers = phoneNumbers
                        )
                    )
                }
            }
            contacts
        }

    override suspend fun getRemoteContacts(): Result<List<Contact>> {
        return try {
            val authToken = getAuthToken()
            val response = scanService.getRemoteContacts(/**/authToken)
            if (response.isSuccessful) {
                val contacts = response.body() ?: emptyList()
                Log.i("ContactRepositoryImpl", "Fetched remote contacts: $contacts")
                Result.success(contacts)
            } else {
                Result.failure(Exception("Remote error: ${response.code()}"))
            }

        } catch (e: Exception) {
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
                organizationName = contactRequest.organization,
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