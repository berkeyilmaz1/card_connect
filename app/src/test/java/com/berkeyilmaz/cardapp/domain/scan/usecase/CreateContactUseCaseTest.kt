package com.berkeyilmaz.cardapp.domain.scan.usecase

import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CreateContactUseCaseTest {

    private lateinit var repository: ContactRepository
    private lateinit var useCase: CreateContactUseCase

    @Before
    fun setUp() {
        repository = mock()
        useCase = CreateContactUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository createContact`() = runTest {
        val request = ContactRequest(fullName = "Ali Veli", organization = "ACME")
        val expected = Result.success(Contact(contactId = "1", fullName = "Ali Veli"))
        whenever(repository.createContact(request)).thenReturn(expected)

        val result = useCase(request)

        assertEquals(expected, result)
        verify(repository).createContact(request)
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        val request = ContactRequest(fullName = "Ali")
        val expected = Result.failure<Contact>(Exception("Network error"))
        whenever(repository.createContact(request)).thenReturn(expected)

        val result = useCase(request)

        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke returns success with created contact`() = runTest {
        val request = ContactRequest(fullName = "Test", phones = listOf("+901234567890"))
        val contact = Contact(contactId = "new-id", fullName = "Test")
        whenever(repository.createContact(request)).thenReturn(Result.success(contact))

        val result = useCase(request)

        assertTrue(result.isSuccess)
        assertEquals("new-id", result.getOrNull()?.contactId)
    }
}
