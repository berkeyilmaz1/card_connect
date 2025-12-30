package com.berkeyilmaz.cardapp.domain.contact.model

import com.berkeyilmaz.cardapp.domain.scan_result.model.Tag
import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("contactId") val contactId: String = "",
    @SerializedName("fullName") val fullName: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("organizationName") val organizationName: String = "",
    @SerializedName("phones") val phones: List<String> = listOf(),
    @SerializedName("emails") val emails: List<String> = listOf(),
    @SerializedName("tags") val tags: List<Tag> = emptyList()
)