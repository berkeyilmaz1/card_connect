package com.berkeyilmaz.cardapp.domain.contact.model

import org.junit.Assert.*
import org.junit.Test

class InternalContactChangesTest {

    private val sampleContact = InternalContact(
        contactId = "1",
        fullName = "Ali Veli",
        phones = listOf("+905551234567")
    )

    @Test
    fun `hasChanges returns false when all lists are empty`() {
        val changes = InternalContactChanges()
        assertFalse(changes.hasChanges)
    }

    @Test
    fun `hasChanges returns true when added list is not empty`() {
        val changes = InternalContactChanges(added = listOf(sampleContact))
        assertTrue(changes.hasChanges)
    }

    @Test
    fun `hasChanges returns true when removed list is not empty`() {
        val changes = InternalContactChanges(removed = listOf(sampleContact))
        assertTrue(changes.hasChanges)
    }

    @Test
    fun `hasChanges returns true when modified list is not empty`() {
        val changes = InternalContactChanges(modified = listOf(sampleContact))
        assertTrue(changes.hasChanges)
    }

    @Test
    fun `hasChanges returns true when all lists have items`() {
        val changes = InternalContactChanges(
            added = listOf(sampleContact),
            removed = listOf(sampleContact),
            modified = listOf(sampleContact)
        )
        assertTrue(changes.hasChanges)
    }

    @Test
    fun `default values are empty lists`() {
        val changes = InternalContactChanges()
        assertTrue(changes.added.isEmpty())
        assertTrue(changes.removed.isEmpty())
        assertTrue(changes.modified.isEmpty())
    }

    @Test
    fun `two identical changes are equal`() {
        val a = InternalContactChanges(added = listOf(sampleContact))
        val b = InternalContactChanges(added = listOf(sampleContact))
        assertEquals(a, b)
    }

    @Test
    fun `different changes are not equal`() {
        val a = InternalContactChanges(added = listOf(sampleContact))
        val b = InternalContactChanges(removed = listOf(sampleContact))
        assertNotEquals(a, b)
    }
}
