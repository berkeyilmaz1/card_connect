package com.berkeyilmaz.cardapp.domain.contact.usecase

import android.content.ContentResolver
import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class MergeContactsUseCaseTest {

    private lateinit var repository: ContactRepository
    private lateinit var useCase: MergeContactsUseCase
    private lateinit var contentResolver: ContentResolver

    @Before
    fun setUp() {
        repository = mock()
        useCase = MergeContactsUseCase(repository)
        contentResolver = mock()
    }

    @Test
    fun `invoke delegates to repository mergeContacts`() = runTest {
        val primary = Contact(contactId = "1", fullName = "Ali Veli")
        val duplicates = listOf(Contact(contactId = "2", fullName = "Ali Veli"))
        val internalDuplicates = listOf(InternalContact(contactId = "ic1", fullName = "Ali Veli", phones = listOf()))
        val expected = Result.success(primary)

        whenever(repository.mergeContacts(contentResolver, primary, duplicates, internalDuplicates))
            .thenReturn(expected)

        val result = useCase(contentResolver, primary, duplicates, internalDuplicates)

        assertEquals(expected, result)
        verify(repository).mergeContacts(contentResolver, primary, duplicates, internalDuplicates)
    }

    @Test
    fun `invoke returns failure when merge fails`() = runTest {
        val primary = Contact(contactId = "1")
        val expected = Result.failure<Contact>(Exception("Merge error"))

        whenever(repository.mergeContacts(contentResolver, primary, emptyList(), emptyList()))
            .thenReturn(expected)

        val result = useCase(contentResolver, primary, emptyList(), emptyList())

        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke handles empty duplicates lists`() = runTest {
        val primary = Contact(contactId = "1", fullName = "Sole Contact")
        val expected = Result.success(primary)

        whenever(repository.mergeContacts(contentResolver, primary, emptyList(), emptyList()))
            .thenReturn(expected)

        val result = useCase(contentResolver, primary, emptyList(), emptyList())

        assertTrue(result.isSuccess)
    }
}
