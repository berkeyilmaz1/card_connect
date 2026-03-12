package com.berkeyilmaz.cardapp.domain.settings.model

import org.junit.Assert.*
import org.junit.Test

class AppThemeTest {

    @Test
    fun `AppTheme has LIGHT value`() {
        assertNotNull(AppTheme.LIGHT)
    }

    @Test
    fun `AppTheme has DARK value`() {
        assertNotNull(AppTheme.DARK)
    }

    @Test
    fun `AppTheme has SYSTEM value`() {
        assertNotNull(AppTheme.SYSTEM)
    }

    @Test
    fun `AppTheme has 3 entries`() {
        assertEquals(3, AppTheme.entries.size)
    }

    @Test
    fun `AppTheme entries contain all expected values`() {
        val entries = AppTheme.entries
        assertTrue(entries.contains(AppTheme.LIGHT))
        assertTrue(entries.contains(AppTheme.DARK))
        assertTrue(entries.contains(AppTheme.SYSTEM))
    }

    @Test
    fun `AppTheme valueOf works for LIGHT`() {
        assertEquals(AppTheme.LIGHT, AppTheme.valueOf("LIGHT"))
    }

    @Test
    fun `AppTheme valueOf works for DARK`() {
        assertEquals(AppTheme.DARK, AppTheme.valueOf("DARK"))
    }

    @Test
    fun `AppTheme valueOf works for SYSTEM`() {
        assertEquals(AppTheme.SYSTEM, AppTheme.valueOf("SYSTEM"))
    }
}
