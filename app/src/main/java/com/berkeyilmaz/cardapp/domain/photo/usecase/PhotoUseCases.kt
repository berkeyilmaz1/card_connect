package com.berkeyilmaz.cardapp.domain.photo.usecase

import com.berkeyilmaz.cardapp.domain.photo.model.Photo
import com.berkeyilmaz.cardapp.domain.photo.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContactPhotoUseCase @Inject constructor(
    private val repo: PhotoRepository
) {
    operator fun invoke(contactId: String, userId: String): Flow<Photo> =
        repo.getPhotosByContactIdAndUserId(contactId, userId)
}

class GetAllPhotosUseCase @Inject constructor(
    private val repo: PhotoRepository
) {
    operator fun invoke(userId: String): Flow<List<Photo>> =
        repo.getAllPhotos(userId)
}

class InsertPhotoUseCase @Inject constructor(
    private val repo: PhotoRepository
) {
    operator fun invoke(photo: Photo) = repo.insertPhoto(photo)
}

class DeletePhotoUseCase @Inject constructor(
    private val repo: PhotoRepository
) {
    operator fun invoke(photo: Photo) = repo.deletePhoto(photo)
}