package com.berkeyilmaz.cardapp.domain.contact.usecase

import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetRemoteContactsUseCaseTest {

    private lateinit var repository: ContactRepository
    private lateinit var useCase: GetRemoteContactsUseCase

    @Before
    fun setUp() {
        repository = mock()
        useCase = GetRemoteContactsUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository getRemoteContacts`() = runTest {
        val contacts = listOf(Contact(contactId = "1", fullName = "Ali"))
        whenever(repository.getRemoteContacts()).thenReturn(Result.success(contacts))

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(contacts, result.getOrNull())
        verify(repository).getRemoteContacts()
    }

    @Test
    fun `invoke returns empty list when no contacts`() = runTest {
        whenever(repository.getRemoteContacts()).thenReturn(Result.success(emptyList()))

        val result = useCase()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }

    @Test
    fun `invoke returns multiple contacts`() = runTest {
        val contacts = listOf(
            Contact(contactId = "1", fullName = "Ali"),
            Contact(contactId = "2", fullName = "Veli"),
            Contact(contactId = "3", fullName = "Ayşe")
        )
        whenever(repository.getRemoteContacts()).thenReturn(Result.success(contacts))

        val result = useCase()

        assertEquals(3, result.getOrNull()?.size)
    }

    @Test
    fun `invoke returns failure on error`() = runTest {
        whenever(repository.getRemoteContacts()).thenReturn(Result.failure(Exception("Network error")))

        val result = useCase()

        assertTrue(result.isFailure)
    }
}
