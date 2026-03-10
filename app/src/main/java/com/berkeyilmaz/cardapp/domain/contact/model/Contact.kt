package com.berkeyilmaz.cardapp.domain.contact.model

import com.berkeyilmaz.cardapp.domain.scan_result.model.SocialMedia
import com.berkeyilmaz.cardapp.domain.scan_result.model.Tag
import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("contactId") val contactId: String = "",
    @SerializedName("internalContactId") val internalContactId: String = "",
    @SerializedName("fullName") val fullName: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("organization") val organization: String = "",
    @SerializedName("phones") val phones: List<String> = listOf(),
    @SerializedName("emails") val emails: List<String> = listOf(),
    @SerializedName("tags") val tags: List<Tag> = emptyList(),
    @SerializedName("note") val note: String = "",
    @SerializedName("address") val address: String = "",
    @SerializedName("imageUrl") val imageUrl: String = "",
    @SerializedName("socialMedias") val socialMedias: List<SocialMedia> = emptyList(),
    @SerializedName("websites") val websites: List<String> = emptyList()
)