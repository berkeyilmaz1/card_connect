package com.berkeyilmaz.cardapp.data.local.mapper

import com.berkeyilmaz.cardapp.data.local.entity.InternalContactEntity
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import org.junit.Assert.*
import org.junit.Test

class InternalContactMapperTest {

    @Test
    fun `toEntity maps all fields correctly`() {
        val contact = InternalContact(
            contactId = "1",
            fullName = "Ali Veli",
            phoneNumbers = listOf("+905551234567"),
            emails = listOf("ali@example.com"),
            websites = listOf("https://ali.com"),
            organization = "ACME Corp",
            title = "Engineer"
        )
        val entity = contact.toEntity()

        assertEquals("1", entity.contactId)
        assertEquals("Ali Veli", entity.fullName)
        assertEquals(listOf("+905551234567"), entity.phoneNumbers)
        assertEquals(listOf("ali@example.com"), entity.emails)
        assertEquals(listOf("https://ali.com"), entity.websites)
        assertEquals("ACME Corp", entity.organization)
        assertEquals("Engineer", entity.title)
    }

    @Test
    fun `toEntity maps null emails to empty list`() {
        val contact = InternalContact(
            contactId = "2",
            fullName = "Test User",
            phoneNumbers = listOf(),
            emails = null
        )
        val entity = contact.toEntity()
        assertEquals(emptyList<String>(), entity.emails)
    }

    @Test
    fun `toEntity maps null websites to empty list`() {
        val contact = InternalContact(
            contactId = "3",
            fullName = "Test User",
            phoneNumbers = listOf(),
            websites = null
        )
        val entity = contact.toEntity()
        assertEquals(emptyList<String>(), entity.websites)
    }

    @Test
    fun `toDomain maps all fields correctly`() {
        val entity = InternalContactEntity(
            contactId = "1",
            fullName = "Ali Veli",
            phoneNumbers = listOf("+905551234567"),
            emails = listOf("ali@example.com"),
            websites = listOf("https://ali.com"),
            organization = "ACME Corp",
            title = "Engineer"
        )
        val contact = entity.toDomain()

        assertEquals("1", contact.contactId)
        assertEquals("Ali Veli", contact.fullName)
        assertEquals(listOf("+905551234567"), contact.phoneNumbers)
        assertEquals(listOf("ali@example.com"), contact.emails)
        assertEquals(listOf("https://ali.com"), contact.websites)
        assertEquals("ACME Corp", contact.organization)
        assertEquals("Engineer", contact.title)
    }

    @Test
    fun `toDomain maps empty emails list to null`() {
        val entity = InternalContactEntity(
            contactId = "2",
            fullName = "Test",
            phoneNumbers = listOf(),
            emails = emptyList(),
            websites = emptyList(),
            organization = null,
            title = null
        )
        val contact = entity.toDomain()
        assertNull(contact.emails)
    }

    @Test
    fun `toDomain maps empty websites list to null`() {
        val entity = InternalContactEntity(
            contactId = "3",
            fullName = "Test",
            phoneNumbers = listOf(),
            emails = emptyList(),
            websites = emptyList(),
            organization = null,
            title = null
        )
        val contact = entity.toDomain()
        assertNull(contact.websites)
    }

    @Test
    fun `toEntityList maps list correctly`() {
        val contacts = listOf(
            InternalContact(contactId = "1", fullName = "Ali", phoneNumbers = listOf()),
            InternalContact(contactId = "2", fullName = "Veli", phoneNumbers = listOf())
        )
        val entities = contacts.toEntityList()
        assertEquals(2, entities.size)
        assertEquals("1", entities[0].contactId)
        assertEquals("2", entities[1].contactId)
    }

    @Test
    fun `toDomainList maps list correctly`() {
        val entities = listOf(
            InternalContactEntity(
                contactId = "1", fullName = "Ali",
                phoneNumbers = listOf(), emails = listOf(), websites = listOf(),
                organization = null, title = null
            ),
            InternalContactEntity(
                contactId = "2", fullName = "Veli",
                phoneNumbers = listOf(), emails = listOf(), websites = listOf(),
                organization = null, title = null
            )
        )
        val contacts = entities.toDomainList()
        assertEquals(2, contacts.size)
        assertEquals("1", contacts[0].contactId)
        assertEquals("2", contacts[1].contactId)
    }

    @Test
    fun `round trip toEntity then toDomain preserves data`() {
        val original = InternalContact(
            contactId = "rt1",
            fullName = "Round Trip",
            phoneNumbers = listOf("+901234567890"),
            emails = listOf("rt@test.com"),
            websites = listOf("https://rt.com"),
            organization = "RT Corp",
            title = "Manager"
        )
        val result = original.toEntity().toDomain()

        assertEquals(original.contactId, result.contactId)
        assertEquals(original.fullName, result.fullName)
        assertEquals(original.phoneNumbers, result.phoneNumbers)
        assertEquals(original.emails, result.emails)
        assertEquals(original.websites, result.websites)
        assertEquals(original.organization, result.organization)
        assertEquals(original.title, result.title)
    }

    @Test
    fun `toEntityList with empty list returns empty list`() {
        val result = emptyList<InternalContact>().toEntityList()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `toDomainList with empty list returns empty list`() {
        val result = emptyList<InternalContactEntity>().toDomainList()
        assertTrue(result.isEmpty())
    }
}
