package com.berkeyilmaz.cardapp.domain.scan_result.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ExtractedData(
    val fullName: String? = null,
    @SerializedName("title") val jobTitle: String? = null,
    val organization: String? = null,
    val phones: List<String> = listOf(),
    val emails: List<String> = listOf(),
    val websites: List<String> = listOf(),
    val addresses: List<String> = listOf(),
    val socialMedia: List<SocialMedia> = listOf(),
    val tags: List<Tag> = listOf(),
    val note: String? = null
) : Serializable

