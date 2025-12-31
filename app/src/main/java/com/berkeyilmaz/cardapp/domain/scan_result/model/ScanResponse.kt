package com.berkeyilmaz.cardapp.domain.scan_result.model

import java.io.Serializable

data class ScanResponse(
    val fullName: String? = null,
    val title: String? = null,
    val organization: String? = null,
    val phones: List<String> = listOf(),
    val emails: List<String> = listOf(),
    val websites: List<String> = listOf(),
    val address: String? = null,
    val socialMedias: List<SocialMedia> = listOf(),
    val tags: List<Tag> = listOf(),
    val note: String? = null,
    var imageUrl: String? = null,
) : Serializable

