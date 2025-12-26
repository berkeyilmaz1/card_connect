package com.berkeyilmaz.cardapp.data.local

import com.berkeyilmaz.cardapp.data.local.dao.PhotoDAO
import com.berkeyilmaz.cardapp.data.local.mapper.toDomain
import com.berkeyilmaz.cardapp.data.local.mapper.toEntity
import com.berkeyilmaz.cardapp.domain.photo.model.Photo
import com.berkeyilmaz.cardapp.domain.photo.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val photoDao: PhotoDAO
) : PhotoRepository {
    override fun getAllPhotos(userId: String): Flow<List<Photo>> {
        return photoDao.getAllPhotos(userId).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getPhotosByContactIdAndUserId(
        contactId: String, userId: String
    ): Flow<Photo> {
        return photoDao.getPhotosByContactIdAndUserId(contactId, userId).map { it.toDomain() }
    }

    override fun insertPhoto(photo: Photo) {
        photoDao.insertPhoto(photo.toEntity())
    }

    override fun deletePhoto(photo: Photo) {
        photoDao.deletePhoto(photo.toEntity())
    }
}
