package com.berkeyilmaz.cardapp.domain.settings.usecase

import com.berkeyilmaz.cardapp.domain.settings.LlmRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LlmUseCasesTest {

    private lateinit var repository: LlmRepository
    private lateinit var getUseLocalLlmUseCase: GetUseLocalLlmUseCase
    private lateinit var setUseLocalLlmUseCase: SetUseLocalLlmUseCase

    @Before
    fun setUp() {
        repository = mock()
        getUseLocalLlmUseCase = GetUseLocalLlmUseCase(repository)
        setUseLocalLlmUseCase = SetUseLocalLlmUseCase(repository)
    }

    @Test
    fun `GetUseLocalLlmUseCase returns true from repository`() = runTest {
        whenever(repository.useLocalLlmFlow).thenReturn(flowOf(true))

        val result = getUseLocalLlmUseCase().first()

        assertTrue(result)
    }

    @Test
    fun `GetUseLocalLlmUseCase returns false from repository`() = runTest {
        whenever(repository.useLocalLlmFlow).thenReturn(flowOf(false))

        val result = getUseLocalLlmUseCase().first()

        assertFalse(result)
    }

    @Test
    fun `SetUseLocalLlmUseCase calls repository with true`() = runTest {
        setUseLocalLlmUseCase(true)

        verify(repository).setUseLocalLlm(true)
    }

    @Test
    fun `SetUseLocalLlmUseCase calls repository with false`() = runTest {
        setUseLocalLlmUseCase(false)

        verify(repository).setUseLocalLlm(false)
    }
}
