package com.berkeyilmaz.cardapp.domain.home.usecase

import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.berkeyilmaz.cardapp.domain.home.HomeRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class HomeUseCasesTest {

    private lateinit var repository: HomeRepository
    private lateinit var useCase: GetCurrentUserUseCase

    @Before
    fun setUp() {
        repository = mock()
        useCase = GetCurrentUserUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository getCurrentUser`() = runTest {
        val mockUser = mock<FirebaseUser>()
        val expected = ResponseState.Success(mockUser)
        whenever(repository.getCurrentUser()).thenReturn(expected)

        val result = useCase()

        assertEquals(expected, result)
        verify(repository).getCurrentUser()
    }

    @Test
    fun `invoke returns Success with null user when not logged in`() = runTest {
        val expected = ResponseState.Success<FirebaseUser?>(null)
        whenever(repository.getCurrentUser()).thenReturn(expected)

        val result = useCase()

        assertTrue(result is ResponseState.Success)
        assertNull((result as ResponseState.Success).data)
    }

    @Test
    fun `invoke returns Error on failure`() = runTest {
        val expected = ResponseState.Error("Not authenticated")
        whenever(repository.getCurrentUser()).thenReturn(expected)

        val result = useCase()

        assertTrue(result is ResponseState.Error)
    }
}
