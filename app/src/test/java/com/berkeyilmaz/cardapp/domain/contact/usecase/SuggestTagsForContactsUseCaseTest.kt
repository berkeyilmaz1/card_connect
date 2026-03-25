package com.berkeyilmaz.cardapp.domain.contact.usecase

import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SuggestTagsForContactsUseCaseTest {

    private lateinit var repository: ContactRepository
    private lateinit var useCase: SuggestTagsForContactsUseCase

    @Before
    fun setUp() {
        repository = mock()
        useCase = SuggestTagsForContactsUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository suggestTagsForNewContact`() = runTest {
        val contacts = listOf(InternalContact(contactId = "1", fullName = "Ali", phones = listOf()))
        val expected = listOf(ContactRequest(fullName = "Ali"))
        whenever(repository.suggestTagsForNewContact(contacts)).thenReturn(expected)

        val result = useCase(contacts)

        assertEquals(expected, result)
        verify(repository).suggestTagsForNewContact(contacts)
    }

    @Test
    fun `invoke returns empty list for empty input`() = runTest {
        whenever(repository.suggestTagsForNewContact(emptyList())).thenReturn(emptyList())

        val result = useCase(emptyList())

        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke returns multiple contact requests`() = runTest {
        val contacts = listOf(
            InternalContact(contactId = "1", fullName = "Ali", phones = listOf()),
            InternalContact(contactId = "2", fullName = "Veli", phones = listOf())
        )
        val expected = listOf(
            ContactRequest(fullName = "Ali"),
            ContactRequest(fullName = "Veli")
        )
        whenever(repository.suggestTagsForNewContact(contacts)).thenReturn(expected)

        val result = useCase(contacts)

        assertEquals(2, result.size)
    }
}
