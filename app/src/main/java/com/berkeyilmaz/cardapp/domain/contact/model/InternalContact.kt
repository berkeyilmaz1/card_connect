package com.berkeyilmaz.cardapp.domain.contact.model

data class InternalContact(
    val internalId: String = "",
    val fullName: String = "",
    val phoneNumbers: List<String> = emptyList(),
)
