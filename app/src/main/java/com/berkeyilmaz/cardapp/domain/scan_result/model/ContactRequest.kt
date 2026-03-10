package com.berkeyilmaz.cardapp.domain.scan_result.model

import com.google.gson.annotations.SerializedName

data class ContactRequest(
    @SerializedName("internalContactId") val internalContactId: String = "",
    @SerializedName("fullName") val fullName: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("organization") val organization: String = "",
    @SerializedName("phones") val phones: List<String> = listOf(),
    @SerializedName("emails") val emails: List<String> = listOf(),
    @SerializedName("websites") val websites: List<String> = listOf(),
    @SerializedName("address") val address: String = "",
    @SerializedName("socialMedias") val socialMedias: List<SocialMedia> = listOf(),
    val tags: List<Tag> = emptyList(),
    val note: String = "",
    val imageUrl: String = "",
    val llmSource: String = ""
)