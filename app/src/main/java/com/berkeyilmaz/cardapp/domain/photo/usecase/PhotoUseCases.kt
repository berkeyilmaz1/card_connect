package com.berkeyilmaz.cardapp.domain.photo.usecase

import com.berkeyilmaz.cardapp.domain.photo.model.Photo
import com.berkeyilmaz.cardapp.domain.photo.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPhotosUseCase @Inject constructor(
    private val repo: PhotoRepository
) {
    suspend operator fun invoke(contactId: String, userId: String): Flow<List<Photo>> =
        repo.getPhotosByContactIdAndUserId(contactId, userId)
}

class InsertPhotoUseCase @Inject constructor(
    private val repo: PhotoRepository
) {
    suspend operator fun invoke(photo: Photo) = repo.insertPhoto(photo)
}

class DeletePhotoUseCase @Inject constructor(
    private val repo: PhotoRepository
) {
    suspend operator fun invoke(photo: Photo) = repo.deletePhoto(photo)
}