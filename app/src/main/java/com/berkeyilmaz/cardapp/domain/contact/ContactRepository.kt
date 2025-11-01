package com.berkeyilmaz.cardapp.domain.contact

import android.content.ContentResolver
import com.berkeyilmaz.cardapp.domain.contact.model.Contact

interface ContactRepository {
    suspend fun getContacts(contentResolver: ContentResolver): List<Contact>
}