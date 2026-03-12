package com.berkeyilmaz.cardapp.domain.settings.usecase

import com.berkeyilmaz.cardapp.domain.LanguageRepository
import com.berkeyilmaz.cardapp.domain.settings.model.Language
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LanguageUseCasesTest {

    private lateinit var repository: LanguageRepository
    private lateinit var getLanguageUseCase: GetLanguageUseCase
    private lateinit var saveLanguageUseCase: SaveLanguageUseCase

    @Before
    fun setUp() {
        repository = mock()
        getLanguageUseCase = GetLanguageUseCase(repository)
        saveLanguageUseCase = SaveLanguageUseCase(repository)
    }

    @Test
    fun `GetLanguageUseCase returns flow from repository`() = runTest {
        whenever(repository.getLanguage()).thenReturn(flowOf(Language.TURKISH))

        val result = getLanguageUseCase().first()

        assertEquals(Language.TURKISH, result)
    }

    @Test
    fun `GetLanguageUseCase returns ENGLISH language`() = runTest {
        whenever(repository.getLanguage()).thenReturn(flowOf(Language.ENGLISH))

        val result = getLanguageUseCase().first()

        assertEquals(Language.ENGLISH, result)
    }

    @Test
    fun `SaveLanguageUseCase calls repository saveLanguage with TURKISH`() = runTest {
        saveLanguageUseCase(Language.TURKISH)

        verify(repository).saveLanguage(Language.TURKISH)
    }

    @Test
    fun `SaveLanguageUseCase calls repository saveLanguage with ENGLISH`() = runTest {
        saveLanguageUseCase(Language.ENGLISH)

        verify(repository).saveLanguage(Language.ENGLISH)
    }
}
