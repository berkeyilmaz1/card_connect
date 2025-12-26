package com.berkeyilmaz.cardapp.data.local.mapper

import com.berkeyilmaz.cardapp.data.local.entity.PhotoEntity
import com.berkeyilmaz.cardapp.domain.photo.model.Photo

fun PhotoEntity.toDomain(): Photo {
    return Photo(
        id = id,
        contactId = contactId,
        userId = userId,
        filePath = filePath,
        createdAt = createdAt
    )
}

fun Photo.toEntity(): PhotoEntity {
    return PhotoEntity(
        id = this.id ?: 0,
        contactId = contactId,
        userId = userId,
        filePath = filePath
    )
}
