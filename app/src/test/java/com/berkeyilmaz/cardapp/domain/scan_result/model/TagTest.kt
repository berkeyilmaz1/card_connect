package com.berkeyilmaz.cardapp.domain.scan_result.model

import org.junit.Assert.*
import org.junit.Test

class TagTest {

    @Test
    fun `getCategoryEnum returns correct category for WORK`() {
        val tag = Tag(category = "WORK", name = "Developer")
        assertEquals(TagCategory.WORK, tag.getCategoryEnum())
    }

    @Test
    fun `getCategoryEnum returns PERSONAL for unknown category`() {
        val tag = Tag(category = "UNKNOWN", name = "Something")
        assertEquals(TagCategory.PERSONAL, tag.getCategoryEnum())
    }

    @Test
    fun `getCategoryEnum is case insensitive`() {
        val tag = Tag(category = "health", name = "Doctor")
        assertEquals(TagCategory.HEALTH, tag.getCategoryEnum())
    }

    @Test
    fun `Tag default values are empty strings`() {
        val tag = Tag()
        assertEquals("", tag.category)
        assertEquals("", tag.name)
    }

    @Test
    fun `getCategoryEnum returns SCHOOL for school category`() {
        val tag = Tag(category = "SCHOOL", name = "Student")
        assertEquals(TagCategory.SCHOOL, tag.getCategoryEnum())
    }

    @Test
    fun `two Tags with same values are equal`() {
        val a = Tag(category = "WORK", name = "Dev")
        val b = Tag(category = "WORK", name = "Dev")
        assertEquals(a, b)
    }

    @Test
    fun `two Tags with different names are not equal`() {
        val a = Tag(category = "WORK", name = "Dev")
        val b = Tag(category = "WORK", name = "Designer")
        assertNotEquals(a, b)
    }
}
