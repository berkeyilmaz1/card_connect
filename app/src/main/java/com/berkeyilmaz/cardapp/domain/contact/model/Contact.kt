package com.berkeyilmaz.cardapp.domain.contact.model

data class Contact(
    val id: String, val name: String?, val phoneNumbers: List<String>
)