package com.berkeyilmaz.cardapp.domain.settings.model

import org.junit.Assert.*
import org.junit.Test

class LanguageTest {

    @Test
    fun `fromCode returns TURKISH for tr`() {
        assertEquals(Language.TURKISH, Language.fromCode("tr"))
    }

    @Test
    fun `fromCode returns ENGLISH for en`() {
        assertEquals(Language.ENGLISH, Language.fromCode("en"))
    }

    @Test
    fun `fromCode returns ENGLISH for unknown code`() {
        assertEquals(Language.ENGLISH, Language.fromCode("fr"))
    }

    @Test
    fun `fromCode returns ENGLISH for empty string`() {
        assertEquals(Language.ENGLISH, Language.fromCode(""))
    }

    @Test
    fun `TURKISH has correct code and displayName`() {
        assertEquals("tr", Language.TURKISH.code)
        assertEquals("Türkçe", Language.TURKISH.displayName)
    }

    @Test
    fun `ENGLISH has correct code and displayName`() {
        assertEquals("en", Language.ENGLISH.code)
        assertEquals("English", Language.ENGLISH.displayName)
    }

    @Test
    fun `Language has 2 entries`() {
        assertEquals(2, Language.entries.size)
    }

    @Test
    fun `fromCode is case sensitive`() {
        // "TR" != "tr"
        assertEquals(Language.ENGLISH, Language.fromCode("TR"))
    }
}
