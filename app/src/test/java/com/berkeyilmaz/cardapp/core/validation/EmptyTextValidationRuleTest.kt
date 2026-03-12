package com.berkeyilmaz.cardapp.core.validation

import com.berkeyilmaz.cardapp.core.validation.rules.EmptyTextValidationRule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EmptyTextValidationRuleTest {

    private lateinit var rule: EmptyTextValidationRule

    @Before
    fun setUp() {
        rule = EmptyTextValidationRule(errorMessageRes = 0)
    }

    @Test
    fun `validate returns true for non-empty text`() {
        assertTrue(rule.validate("hello"))
    }

    @Test
    fun `validate returns false for empty text`() {
        assertFalse(rule.validate(""))
    }

    @Test
    fun `validate returns true for text with spaces`() {
        assertTrue(rule.validate("   "))
    }

    @Test
    fun `validate returns true for single character`() {
        assertTrue(rule.validate("a"))
    }

    @Test
    fun `validate returns true for numeric text`() {
        assertTrue(rule.validate("123"))
    }

    @Test
    fun `errorMessageRes is set correctly`() {
        val customRule = EmptyTextValidationRule(errorMessageRes = 999)
        assertEquals(999, customRule.errorMessageRes)
    }

    @Test
    fun `rule implements BaseValidationRule`() {
        assertTrue(rule is BaseValidationRule)
    }

    @Test
    fun `validate returns true for long text`() {
        val longText = "a".repeat(1000)
        assertTrue(rule.validate(longText))
    }
}
