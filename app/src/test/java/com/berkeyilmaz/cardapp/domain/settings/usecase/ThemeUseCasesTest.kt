package com.berkeyilmaz.cardapp.domain.settings.usecase

import com.berkeyilmaz.cardapp.domain.settings.ThemeRepository
import com.berkeyilmaz.cardapp.domain.settings.model.AppTheme
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ThemeUseCasesTest {

    private lateinit var repository: ThemeRepository
    private lateinit var getThemeUseCase: GetThemeUseCase
    private lateinit var setThemeUseCase: SetThemeUseCase

    @Before
    fun setUp() {
        repository = mock()
        getThemeUseCase = GetThemeUseCase(repository)
        setThemeUseCase = SetThemeUseCase(repository)
    }

    @Test
    fun `GetThemeUseCase invoke returns flow from repository`() = runTest {
        whenever(repository.themeFlow).thenReturn(flowOf(AppTheme.DARK))

        val result = getThemeUseCase().first()

        assertEquals(AppTheme.DARK, result)
    }

    @Test
    fun `GetThemeUseCase invoke returns LIGHT theme`() = runTest {
        whenever(repository.themeFlow).thenReturn(flowOf(AppTheme.LIGHT))

        val result = getThemeUseCase().first()

        assertEquals(AppTheme.LIGHT, result)
    }

    @Test
    fun `GetThemeUseCase invoke returns SYSTEM theme`() = runTest {
        whenever(repository.themeFlow).thenReturn(flowOf(AppTheme.SYSTEM))

        val result = getThemeUseCase().first()

        assertEquals(AppTheme.SYSTEM, result)
    }

    @Test
    fun `SetThemeUseCase invoke calls repository setTheme`() = runTest {
        setThemeUseCase(AppTheme.DARK)

        verify(repository).setTheme(AppTheme.DARK)
    }

    @Test
    fun `SetThemeUseCase invoke calls repository with LIGHT theme`() = runTest {
        setThemeUseCase(AppTheme.LIGHT)

        verify(repository).setTheme(AppTheme.LIGHT)
    }

    @Test
    fun `SetThemeUseCase invoke calls repository with SYSTEM theme`() = runTest {
        setThemeUseCase(AppTheme.SYSTEM)

        verify(repository).setTheme(AppTheme.SYSTEM)
    }
}
