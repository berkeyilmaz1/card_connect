package com.berkeyilmaz.cardapp.domain.photo.model

data class Photo(
    val id: Int? = null, val contactId: String, val userId: String, val filePath: String
)
