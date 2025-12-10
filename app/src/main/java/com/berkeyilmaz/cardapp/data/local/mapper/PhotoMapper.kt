package com.berkeyilmaz.cardapp.data.local.mapper

import com.berkeyilmaz.cardapp.data.local.entity.PhotoEntity
import com.berkeyilmaz.cardapp.domain.photo.model.Photo

fun PhotoEntity.toDomain(): Photo {
    return Photo(
        id = id,
        contactId = contactId,
        userId = userId,
        filePath = filePath
    )
}

fun Photo.toEntity(): PhotoEntity {
    return PhotoEntity(
        id = id,
        contactId = contactId,
        userId = userId,
        filePath = filePath
    )
}
