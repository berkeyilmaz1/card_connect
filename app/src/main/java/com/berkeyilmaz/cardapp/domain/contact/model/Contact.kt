package com.berkeyilmaz.cardapp.domain.contact.model

import com.google.gson.annotations.SerializedName

data class Contact(
    val contactId: String = "",
    val fullName: String = "",
    val title: String = "",
    val organizationName: String = "",
    val phone: String = "",
    val email: String = "",
    @SerializedName("tags") val tags: List<ContactTag> = emptyList()
) {
    // tags'i groups Map'ine çeviren helper property
    val groups: Map<String, List<String>>
        get() = tags.groupBy { it.category }.mapValues { entry -> entry.value.map { it.name } }
}

data class ContactTag(
    @SerializedName("tagId") val tagId: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("category") val category: String = ""
)