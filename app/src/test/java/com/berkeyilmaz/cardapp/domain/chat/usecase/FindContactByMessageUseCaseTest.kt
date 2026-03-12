package com.berkeyilmaz.cardapp.domain.chat.usecase

import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FindContactByMessageUseCaseTest {

    private lateinit var repository: ContactRepository
    private lateinit var useCase: FindContactByMessageUseCase

    @Before
    fun setUp() {
        repository = mock()
        useCase = FindContactByMessageUseCase(repository)
    }

    @Test
    fun `invoke returns NoResult when repository returns empty list`() = runTest {
        val contacts = listOf<Contact>()
        whenever(repository.searchContactThatUserAsked("find Ali", contacts)).thenReturn(emptyList())

        val result = useCase("find Ali", contacts)

        assertEquals(AIContactResult.NoResult, result)
    }

    @Test
    fun `invoke returns Single when repository returns one contact`() = runTest {
        val contact = Contact(contactId = "1", fullName = "Ali Veli")
        val contacts = listOf(contact)
        whenever(repository.searchContactThatUserAsked("find Ali", contacts)).thenReturn(listOf(contact))

        val result = useCase("find Ali", contacts)

        assertTrue(result is AIContactResult.Single)
        assertEquals(contact, (result as AIContactResult.Single).contact)
    }

    @Test
    fun `invoke returns Multiple when repository returns more than one contact`() = runTest {
        val contact1 = Contact(contactId = "1", fullName = "Ali Veli")
        val contact2 = Contact(contactId = "2", fullName = "Ali Demir")
        val contacts = listOf(contact1, contact2)
        whenever(repository.searchContactThatUserAsked("find Ali", contacts)).thenReturn(listOf(contact1, contact2))

        val result = useCase("find Ali", contacts)

        assertTrue(result is AIContactResult.Multiple)
        assertEquals(2, (result as AIContactResult.Multiple).contacts.size)
    }

    @Test
    fun `invoke calls repository with correct parameters`() = runTest {
        val contacts = listOf(Contact(contactId = "1"))
        whenever(repository.searchContactThatUserAsked("test query", contacts)).thenReturn(emptyList())

        useCase("test query", contacts)

        verify(repository).searchContactThatUserAsked("test query", contacts)
    }

    @Test
    fun `AIContactResult NoResult is data object`() {
        assertEquals(AIContactResult.NoResult, AIContactResult.NoResult)
    }

    @Test
    fun `AIContactResult Single holds correct contact`() {
        val contact = Contact(contactId = "test")
        val single = AIContactResult.Single(contact)
        assertEquals(contact, single.contact)
    }

    @Test
    fun `AIContactResult Multiple holds correct contacts`() {
        val contacts = listOf(Contact(contactId = "1"), Contact(contactId = "2"))
        val multiple = AIContactResult.Multiple(contacts)
        assertEquals(contacts, multiple.contacts)
    }
}
