package com.berkeyilmaz.cardapp.domain.contact.model

data class FoundContact(
    val contactId: String,
    val fullName: String,
    val phone: String,
    val reason: String
)

