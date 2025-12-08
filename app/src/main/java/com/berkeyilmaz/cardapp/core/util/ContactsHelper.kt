package com.berkeyilmaz.cardapp.core.util

import android.content.ContentProviderOperation
import android.content.Context
import android.provider.ContactsContract
import android.util.Log

object ContactsHelper {

    private const val TAG = "ContactsHelper"
    private const val RAW_CONTACT_ID_INDEX = 0

    fun addContactToPhone(
        context: Context,
        displayName: String?,
        phoneNumber: String?,
        email: String?,
        company: String?,
        jobTitle: String?,
        address: String?,
        website: String?,
        notes: String?
    ): Boolean = runCatching {

        val ops = arrayListOf<ContentProviderOperation>()

        ops.add(createRawContact())

        displayName.addIfNotBlank {
            ops.add(insertName(it))
        }

        phoneNumber.addIfNotBlank {
            ops.add(insertPhone(it))
        }

        email.addIfNotBlank {
            ops.add(insertEmail(it))
        }

        if (!company.isNullOrBlank() || !jobTitle.isNullOrBlank()) {
            ops.add(insertOrganization(company, jobTitle))
        }

        address.addIfNotBlank {
            ops.add(insertAddress(it))
        }

        website.addIfNotBlank {
            ops.add(insertWebsite(it))
        }

        notes.addIfNotBlank {
            ops.add(insertNotes(it))
        }

        context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)

        Log.i(TAG, "Contact successfully added.")
        true

    }.getOrElse { error ->
        Log.e(TAG, "Error adding contact: ${error.message}", error)
        false
    }

    // --- PRIVATE HELPERS ---

    private fun createRawContact(): ContentProviderOperation =
        ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
            .build()

    private fun insertName(name: String) = buildInsertOperation(
        mimeType = ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
    ) {
        withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
    }

    private fun insertPhone(phone: String) = buildInsertOperation(
        mimeType = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
    ) {
        withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
        withValue(
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
        )
    }

    private fun insertEmail(email: String) = buildInsertOperation(
        mimeType = ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
    ) {
        withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
        withValue(
            ContactsContract.CommonDataKinds.Email.TYPE,
            ContactsContract.CommonDataKinds.Email.TYPE_WORK
        )
    }

    private fun insertOrganization(company: String?, jobTitle: String?) = buildInsertOperation(
        mimeType = ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE,
    ) {
        withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
        withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
        withValue(
            ContactsContract.CommonDataKinds.Organization.TYPE,
            ContactsContract.CommonDataKinds.Organization.TYPE_WORK
        )
    }

    private fun insertAddress(address: String) = buildInsertOperation(
        mimeType = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE,
    ) {
        withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address)
        withValue(
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK
        )
    }

    private fun insertWebsite(url: String) = buildInsertOperation(
        mimeType = ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE,
    ) {
        withValue(ContactsContract.CommonDataKinds.Website.URL, url)
        withValue(
            ContactsContract.CommonDataKinds.Website.TYPE,
            ContactsContract.CommonDataKinds.Website.TYPE_WORK
        )
    }

    private fun insertNotes(note: String) = buildInsertOperation(
        mimeType = ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE,
    ) {
        withValue(ContactsContract.CommonDataKinds.Note.NOTE, note)
    }

    // --- GENERIC BUILDER ---

    private fun buildInsertOperation(
        mimeType: String,
        block: ContentProviderOperation.Builder.() -> Unit
    ): ContentProviderOperation {
        return ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, RAW_CONTACT_ID_INDEX)
            .withValue(ContactsContract.Data.MIMETYPE, mimeType)
            .apply(block)
            .build()
    }

    // --- EXTENSION ---

    private inline fun String?.addIfNotBlank(action: (String) -> Unit) {
        if (!this.isNullOrBlank()) action(this)
    }
}
