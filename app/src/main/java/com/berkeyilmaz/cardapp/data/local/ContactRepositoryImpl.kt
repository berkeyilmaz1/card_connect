package com.berkeyilmaz.cardapp.data.local

import android.content.ContentResolver
import android.provider.ContactsContract
import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor() : ContactRepository {

    override suspend fun getContacts(contentResolver: ContentResolver): List<Contact> = withContext(
        Dispatchers.IO
    ) {
        val contacts = mutableListOf<Contact>()

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

                contacts.add(Contact(id, name, phoneNumbers))
            }
        }
        contacts
    }
}
