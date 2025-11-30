package com.berkeyilmaz.cardapp.domain.contact.model

import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("user_id") val userId: String,
    @SerializedName("company_id") val companyId: String?,
    @SerializedName("scan_id") val scanId: String?,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("phone_numbers") val phoneNumbers: List<String>? = null,
    val title: String?,
    @SerializedName("internal_id") val internalId: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    val tags: List<ContactTag>?
)

val groupA = ContactTag(id = "g1", name = "Group A")
val groupB = ContactTag(id = "g2", name = "Group B")
val groupC = ContactTag(id = "g3", name = "Group C")
val groupD = ContactTag(id = "g4", name = "Group D")

val baseContact = Contact(
    userId = "defaultUser",
    companyId = null,
    scanId = null,
    fullName = "Default Name",
    title = "Title placeholder",
    internalId = null,
    createdAt = "2025-01-01",
    updatedAt = "2025-01-01",
    tags = emptyList()
)

val contacts = listOf(
    baseContact.copy(userId = "u1", fullName = "Alice", tags = listOf(groupA)),
    baseContact.copy(userId = "u2", fullName = "Bob", tags = listOf(groupA)),
    baseContact.copy(userId = "u3", fullName = "Charlie", tags = listOf(groupB)),
    baseContact.copy(userId = "u4", fullName = "David", tags = listOf(groupB)),
    baseContact.copy(userId = "u5", fullName = "Eva", tags = listOf(groupC)),
    baseContact.copy(userId = "u6", fullName = "Frank", tags = listOf(groupC)),
    baseContact.copy(userId = "u7", fullName = "Grace", tags = listOf(groupD)),
    baseContact.copy(userId = "u8", fullName = "Hank", tags = listOf(groupD)),
    baseContact.copy(userId = "u9", fullName = "Ivy", tags = listOf(groupA)),
    baseContact.copy(
        userId = "1u10", fullName = "AAAAAJack", tags = listOf(groupB, groupC, groupA, groupD)
    )
)