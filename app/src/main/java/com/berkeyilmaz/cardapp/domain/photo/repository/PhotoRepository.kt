package com.berkeyilmaz.cardapp.domain.photo.repository

import com.berkeyilmaz.cardapp.domain.photo.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    suspend fun getPhotosByContactIdAndUserId(contactId: String, userId: String): Flow<List<Photo>>
    suspend fun insertPhoto(photo: Photo)
    suspend fun deletePhoto(photo: Photo)
}