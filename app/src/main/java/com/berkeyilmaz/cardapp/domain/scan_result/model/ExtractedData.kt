package com.berkeyilmaz.cardapp.domain.scan_result.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ExtractedData(
    @SerializedName("fullName") var fullName: String? = null,
    @SerializedName("title") var jobTitle: String? = null,
    @SerializedName("organization") var organization: String? = null,
    @SerializedName("phones") var phones: List<String> = listOf(),
    @SerializedName("emails") var emails: List<String> = listOf(),
    @SerializedName("websites") var websites: List<String> = listOf(),
    @SerializedName("addresses") var addresses: List<String> = listOf(),
    @SerializedName("socialMedia") var socialMedia: List<SocialMedia> = listOf(),
    @SerializedName("tags") var tags: List<String> = listOf(),
    @SerializedName("note") var note: String? = null
) : Serializable
