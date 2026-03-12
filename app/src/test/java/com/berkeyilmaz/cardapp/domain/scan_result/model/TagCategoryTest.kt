package com.berkeyilmaz.cardapp.domain.scan_result.model

import org.junit.Assert.*
import org.junit.Test

class TagCategoryTest {

    @Test
    fun `fromString returns WORK for work string`() {
        assertEquals(TagCategory.WORK, TagCategory.fromString("WORK"))
    }

    @Test
    fun `fromString is case insensitive`() {
        assertEquals(TagCategory.WORK, TagCategory.fromString("work"))
        assertEquals(TagCategory.SCHOOL, TagCategory.fromString("school"))
        assertEquals(TagCategory.HEALTH, TagCategory.fromString("Health"))
    }

    @Test
    fun `fromString returns PERSONAL for unknown value`() {
        assertEquals(TagCategory.PERSONAL, TagCategory.fromString("unknown"))
    }

    @Test
    fun `fromString returns PERSONAL for null`() {
        assertEquals(TagCategory.PERSONAL, TagCategory.fromString(null))
    }

    @Test
    fun `fromString returns PERSONAL for empty string`() {
        assertEquals(TagCategory.PERSONAL, TagCategory.fromString(""))
    }

    @Test
    fun `all categories have display names`() {
        TagCategory.entries.forEach { category ->
            assertTrue(category.displayName.isNotEmpty())
        }
    }

    @Test
    fun `fromString returns SERVICES for SERVICES string`() {
        assertEquals(TagCategory.SERVICES, TagCategory.fromString("SERVICES"))
    }

    @Test
    fun `fromString returns EVENTS for EVENTS string`() {
        assertEquals(TagCategory.EVENTS, TagCategory.fromString("events"))
    }

    @Test
    fun `fromString returns PERSONAL for PERSONAL string`() {
        assertEquals(TagCategory.PERSONAL, TagCategory.fromString("personal"))
    }

    @Test
    fun `TagCategory has 6 entries`() {
        assertEquals(6, TagCategory.entries.size)
    }
}
