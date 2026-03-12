package com.berkeyilmaz.cardapp.domain.contact.model

import org.junit.Assert.*
import org.junit.Test

class DuplicateContactGroupTest {

    private val contact1 = Contact(contactId = "1", fullName = "Ali Veli", phones = listOf("+905551111111"))
    private val contact2 = Contact(contactId = "2", fullName = "Ali Veli", phones = listOf("+905551111111"))
    private val internalContact = InternalContact(contactId = "ic1", fullName = "Ali Veli", phoneNumbers = listOf("+905551111111"))

    @Test
    fun `DuplicateContactGroup created with PHONE reason`() {
        val group = DuplicateContactGroup(
            contacts = listOf(contact1, contact2),
            matchReason = DuplicateMatchReason.PHONE,
            sharedValue = "+905551111111"
        )
        assertEquals(DuplicateMatchReason.PHONE, group.matchReason)
        assertEquals("+905551111111", group.sharedValue)
        assertEquals(2, group.contacts.size)
    }

    @Test
    fun `DuplicateContactGroup created with EMAIL reason`() {
        val group = DuplicateContactGroup(
            contacts = listOf(contact1),
            matchReason = DuplicateMatchReason.EMAIL,
            sharedValue = "ali@example.com"
        )
        assertEquals(DuplicateMatchReason.EMAIL, group.matchReason)
    }

    @Test
    fun `DuplicateContactGroup created with PHONE_AND_EMAIL reason`() {
        val group = DuplicateContactGroup(
            contacts = listOf(contact1, contact2),
            matchReason = DuplicateMatchReason.PHONE_AND_EMAIL,
            sharedValue = "+905551111111"
        )
        assertEquals(DuplicateMatchReason.PHONE_AND_EMAIL, group.matchReason)
    }

    @Test
    fun `internalContacts defaults to empty list`() {
        val group = DuplicateContactGroup(
            contacts = listOf(contact1),
            matchReason = DuplicateMatchReason.PHONE,
            sharedValue = "+905551111111"
        )
        assertTrue(group.internalContacts.isEmpty())
    }

    @Test
    fun `internalContacts can be set`() {
        val group = DuplicateContactGroup(
            contacts = listOf(contact1),
            internalContacts = listOf(internalContact),
            matchReason = DuplicateMatchReason.PHONE,
            sharedValue = "+905551111111"
        )
        assertEquals(1, group.internalContacts.size)
        assertEquals("ic1", group.internalContacts.first().contactId)
    }

    @Test
    fun `DuplicateMatchReason has correct values`() {
        val values = DuplicateMatchReason.entries
        assertTrue(values.contains(DuplicateMatchReason.PHONE))
        assertTrue(values.contains(DuplicateMatchReason.EMAIL))
        assertTrue(values.contains(DuplicateMatchReason.PHONE_AND_EMAIL))
        assertEquals(3, values.size)
    }
}
