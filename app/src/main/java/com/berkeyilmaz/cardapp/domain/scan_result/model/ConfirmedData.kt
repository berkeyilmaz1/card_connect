package com.berkeyilmaz.cardapp.domain.scan_result.model

import com.google.gson.annotations.SerializedName

data class ConfirmedData(
    @SerializedName("fullName") var fullName: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("organization") var organization: String? = null,
    @SerializedName("phones") var phones: List<String> = listOf(),
    @SerializedName("emails") var emails: List<String> = listOf(),
    @SerializedName("websites") var websites: List<String> = listOf(),
    @SerializedName("addresses") var addresses: List<String> = listOf(),
    @SerializedName("socialMedia") var socialMedia: List<SocialMedia> = listOf(),
    @SerializedName("tags") var tags: List<String> = listOf(),
)