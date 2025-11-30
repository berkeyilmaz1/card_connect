package com.berkeyilmaz.cardapp.domain.contact.model

import com.google.gson.annotations.SerializedName

data class ContactTag(
    @SerializedName("tag_id") val id: String,
    val name: String
)
