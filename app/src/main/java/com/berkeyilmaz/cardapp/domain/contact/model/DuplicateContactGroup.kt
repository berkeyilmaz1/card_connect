package com.berkeyilmaz.cardapp.domain.contact.model

enum class DuplicateMatchReason { PHONE, EMAIL, PHONE_AND_EMAIL }

data class DuplicateContactGroup(
    val contacts: List<Contact>,
    val internalContacts: List<InternalContact> = emptyList(),
    val matchReason: DuplicateMatchReason,
    val sharedValue: String
)
