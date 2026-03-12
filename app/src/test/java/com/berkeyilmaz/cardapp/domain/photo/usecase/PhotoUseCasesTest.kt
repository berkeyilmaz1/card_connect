package com.berkeyilmaz.cardapp.domain.photo.usecase

import com.berkeyilmaz.cardapp.domain.photo.model.Photo
import com.berkeyilmaz.cardapp.domain.photo.repository.PhotoRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PhotoUseCasesTest {

    private lateinit var repository: PhotoRepository
    private lateinit var getContactPhotoUseCase: GetContactPhotoUseCase
    private lateinit var getAllPhotosUseCase: GetAllPhotosUseCase
    private lateinit var insertPhotoUseCase: InsertPhotoUseCase
    private lateinit var deletePhotoUseCase: DeletePhotoUseCase

    private val samplePhoto = Photo(
        id = 1,
        contactId = "contact-1",
        userId = "user-1",
        filePath = "/storage/photo.jpg",
        createdAt = 1234567890L
    )

    @Before
    fun setUp() {
        repository = mock()
        getContactPhotoUseCase = GetContactPhotoUseCase(repository)
        getAllPhotosUseCase = GetAllPhotosUseCase(repository)
        insertPhotoUseCase = InsertPhotoUseCase(repository)
        deletePhotoUseCase = DeletePhotoUseCase(repository)
    }

    @Test
    fun `GetContactPhotoUseCase returns photo flow from repository`() = runTest {
        whenever(repository.getPhotosByContactIdAndUserId("contact-1", "user-1"))
            .thenReturn(flowOf(samplePhoto))

        val result = getContactPhotoUseCase("contact-1", "user-1").first()

        assertEquals(samplePhoto, result)
        verify(repository).getPhotosByContactIdAndUserId("contact-1", "user-1")
    }

    @Test
    fun `GetAllPhotosUseCase returns all photos flow from repository`() = runTest {
        val photos = listOf(samplePhoto)
        whenever(repository.getAllPhotos("user-1")).thenReturn(flowOf(photos))

        val result = getAllPhotosUseCase("user-1").first()

        assertEquals(photos, result)
        verify(repository).getAllPhotos("user-1")
    }

    @Test
    fun `GetAllPhotosUseCase returns empty list when no photos`() = runTest {
        whenever(repository.getAllPhotos("user-1")).thenReturn(flowOf(emptyList()))

        val result = getAllPhotosUseCase("user-1").first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `InsertPhotoUseCase calls repository insertPhoto`() {
        insertPhotoUseCase(samplePhoto)

        verify(repository).insertPhoto(samplePhoto)
    }

    @Test
    fun `DeletePhotoUseCase calls repository deletePhoto`() {
        deletePhotoUseCase(samplePhoto)

        verify(repository).deletePhoto(samplePhoto)
    }
}
