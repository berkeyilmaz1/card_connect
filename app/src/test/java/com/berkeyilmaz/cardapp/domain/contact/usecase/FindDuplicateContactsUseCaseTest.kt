package com.berkeyilmaz.cardapp.domain.contact.usecase

import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.contact.model.DuplicateContactGroup
import com.berkeyilmaz.cardapp.domain.contact.model.DuplicateMatchReason
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FindDuplicateContactsUseCaseTest {

    private lateinit var repository: ContactRepository
    private lateinit var useCase: FindDuplicateContactsUseCase

    @Before
    fun setUp() {
        repository = mock()
        useCase = FindDuplicateContactsUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository findDuplicateContacts`() = runTest {
        val remoteContacts = listOf(Contact(contactId = "1", phones = listOf("+905551111111")))
        val internalContacts = listOf(InternalContact(contactId = "ic1", phones = listOf("+905551111111")))
        val expected = listOf(
            DuplicateContactGroup(
                contacts = remoteContacts,
                internalContacts = internalContacts,
                matchReason = DuplicateMatchReason.PHONE,
                sharedValue = "+905551111111"
            )
        )
        whenever(repository.findDuplicateContacts(remoteContacts, internalContacts)).thenReturn(expected)

        val result = useCase(remoteContacts, internalContacts)

        assertEquals(expected, result)
        verify(repository).findDuplicateContacts(remoteContacts, internalContacts)
    }

    @Test
    fun `invoke returns empty list when no duplicates`() = runTest {
        val remoteContacts = listOf(Contact(contactId = "1"))
        val internalContacts = listOf(InternalContact(contactId = "ic1", phones = listOf()))
        whenever(repository.findDuplicateContacts(remoteContacts, internalContacts)).thenReturn(emptyList())

        val result = useCase(remoteContacts, internalContacts)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke passes empty lists to repository`() = runTest {
        whenever(repository.findDuplicateContacts(emptyList(), emptyList())).thenReturn(emptyList())

        val result = useCase(emptyList(), emptyList())

        assertTrue(result.isEmpty())
        verify(repository).findDuplicateContacts(emptyList(), emptyList())
    }
}
