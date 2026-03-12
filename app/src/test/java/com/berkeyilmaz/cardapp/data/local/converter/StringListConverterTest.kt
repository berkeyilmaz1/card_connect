package com.berkeyilmaz.cardapp.data.local.converter

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class StringListConverterTest {

    private lateinit var converter: StringListConverter

    @Before
    fun setUp() {
        converter = StringListConverter()
    }

    @Test
    fun `fromStringList converts list to JSON`() {
        val list = listOf("a", "b", "c")
        val json = converter.fromStringList(list)
        assertTrue(json.contains("a"))
        assertTrue(json.contains("b"))
        assertTrue(json.contains("c"))
    }

    @Test
    fun `toStringList parses JSON back to list`() {
        val list = listOf("hello", "world")
        val json = converter.fromStringList(list)
        val result = converter.toStringList(json)
        assertEquals(list, result)
    }

    @Test
    fun `fromStringList with null returns empty JSON array`() {
        val json = converter.fromStringList(null)
        val result = converter.toStringList(json)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `fromStringList with empty list returns empty JSON array`() {
        val json = converter.fromStringList(emptyList())
        val result = converter.toStringList(json)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `toStringList with null returns empty list`() {
        val result = converter.toStringList(null)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `toStringList with empty string returns empty list`() {
        val result = converter.toStringList("")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `round trip preserves phone numbers`() {
        val phones = listOf("+905551234567", "+905559876543")
        val json = converter.fromStringList(phones)
        val result = converter.toStringList(json)
        assertEquals(phones, result)
    }

    @Test
    fun `round trip preserves emails`() {
        val emails = listOf("user@example.com", "test@test.org")
        val json = converter.fromStringList(emails)
        val result = converter.toStringList(json)
        assertEquals(emails, result)
    }

    @Test
    fun `round trip preserves single element list`() {
        val list = listOf("only one")
        val json = converter.fromStringList(list)
        val result = converter.toStringList(json)
        assertEquals(list, result)
    }

    @Test
    fun `toStringList with valid JSON array returns list`() {
        val result = converter.toStringList("[\"a\",\"b\"]")
        assertEquals(listOf("a", "b"), result)
    }
}
