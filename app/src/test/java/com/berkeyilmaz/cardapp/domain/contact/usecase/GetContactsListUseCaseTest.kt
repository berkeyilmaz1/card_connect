package com.berkeyilmaz.cardapp.domain.contact.usecase

import android.content.ContentResolver
import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContactChanges
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetContactsListUseCaseTest {

    private lateinit var repository: ContactRepository
    private lateinit var useCase: GetContactsListUseCase
    private lateinit var contentResolver: ContentResolver

    @Before
    fun setUp() {
        repository = mock()
        useCase = GetContactsListUseCase(repository)
        contentResolver = mock()
    }

    @Test
    fun `invoke delegates to repository getInternalContactsWithChanges`() = runTest {
        val contacts = listOf(InternalContact(contactId = "1", fullName = "Ali", phones = listOf()))
        val changes = InternalContactChanges(added = contacts)
        val expected = Pair(contacts, changes)

        whenever(repository.getInternalContactsWithChanges(contentResolver)).thenReturn(expected)

        val result = useCase(contentResolver)

        assertEquals(expected, result)
        verify(repository).getInternalContactsWithChanges(contentResolver)
    }

    @Test
    fun `invoke returns empty pair when no contacts`() = runTest {
        val expected = Pair(emptyList<InternalContact>(), InternalContactChanges())
        whenever(repository.getInternalContactsWithChanges(contentResolver)).thenReturn(expected)

        val result = useCase(contentResolver)

        assertTrue(result.first.isEmpty())
        assertFalse(result.second.hasChanges)
    }

    @Test
    fun `invoke returns contacts with no changes`() = runTest {
        val contacts = listOf(
            InternalContact(contactId = "1", fullName = "Ali", phones = listOf()),
            InternalContact(contactId = "2", fullName = "Veli", phones = listOf())
        )
        val changes = InternalContactChanges()
        whenever(repository.getInternalContactsWithChanges(contentResolver))
            .thenReturn(Pair(contacts, changes))

        val result = useCase(contentResolver)

        assertEquals(2, result.first.size)
        assertFalse(result.second.hasChanges)
    }
}
