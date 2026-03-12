package com.berkeyilmaz.cardapp.core.common

import org.junit.Assert.*
import org.junit.Test

class ResponseStateTest {

    @Test
    fun `Success holds data correctly`() {
        val state = ResponseState.Success(data = 42)
        assertEquals(42, state.data)
        assertNull(state.message)
    }

    @Test
    fun `Success holds data and message correctly`() {
        val state = ResponseState.Success(data = "hello", message = "OK")
        assertEquals("hello", state.data)
        assertEquals("OK", state.message)
    }

    @Test
    fun `Error holds message correctly`() {
        val state = ResponseState.Error("Something went wrong")
        assertEquals("Something went wrong", state.message)
    }

    @Test
    fun `Success is instance of ResponseState`() {
        val state: ResponseState<Int> = ResponseState.Success(1)
        assertTrue(state is ResponseState.Success)
        assertFalse(state is ResponseState.Error)
    }

    @Test
    fun `Error is instance of ResponseState`() {
        val state: ResponseState<Nothing> = ResponseState.Error("err")
        assertTrue(state is ResponseState.Error)
        assertFalse(state is ResponseState.Success)
    }

    @Test
    fun `Success with null message defaults to null`() {
        val state = ResponseState.Success(data = listOf(1, 2, 3))
        assertNull(state.message)
        assertEquals(3, state.data.size)
    }

    @Test
    fun `Two Success states with same data are equal`() {
        val a = ResponseState.Success(data = "test")
        val b = ResponseState.Success(data = "test")
        assertEquals(a, b)
    }

    @Test
    fun `Two Error states with same message are equal`() {
        val a = ResponseState.Error("error")
        val b = ResponseState.Error("error")
        assertEquals(a, b)
    }

    @Test
    fun `Error states with different messages are not equal`() {
        val a = ResponseState.Error("error1")
        val b = ResponseState.Error("error2")
        assertNotEquals(a, b)
    }
}
