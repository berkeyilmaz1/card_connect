package com.berkeyilmaz.cardapp.domain.photo.repository

import com.berkeyilmaz.cardapp.domain.photo.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getAllPhotos(userId: String): Flow<List<Photo>>
    fun getPhotosByContactIdAndUserId(contactId: String, userId: String): Flow<Photo>
    fun insertPhoto(photo: Photo)
    fun deletePhoto(photo: Photo)
}