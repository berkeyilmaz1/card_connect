package com.berkeyilmaz.cardapp.domain.scan_result.model

import com.google.gson.annotations.SerializedName

data class ContactRequest(
    @SerializedName("fullName") val fullName: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("organization") val organization: String,
    @SerializedName("phones") val phones: List<String> = listOf(),
    @SerializedName("emails") val emails: List<String> = listOf(),
    @SerializedName("websites") val websites: List<String> = listOf(),
    @SerializedName("address") val address: String = "",
    //todo: added s suffix
    @SerializedName("socialMedias") val socialMedias: List<SocialMedia> = listOf(),
    val tags: List<Tag> = emptyList(),
    //todo note,imageUrl added
    val note: String = "",
    val imageUrl: String = "",
)