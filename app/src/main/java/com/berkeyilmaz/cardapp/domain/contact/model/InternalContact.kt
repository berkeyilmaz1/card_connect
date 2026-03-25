package com.berkeyilmaz.cardapp.domain.contact.model

data class InternalContact(
    val contactId: String = "",
    val fullName: String = "",
    val phoneNumbers: List<String> = emptyList(),
    val emails: List<String>? = null,
    val websites: List<String>? = null,
    val organization: String? = null,
    val title: String? = null,
    val address: String? = null,
    val note: String? = null,
    val isInternalContact: Boolean = true
)
