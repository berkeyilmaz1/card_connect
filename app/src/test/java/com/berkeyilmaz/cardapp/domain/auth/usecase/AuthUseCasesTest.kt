package com.berkeyilmaz.cardapp.domain.auth.usecase

import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.berkeyilmaz.cardapp.domain.auth.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AuthUseCasesTest {

    private lateinit var repository: AuthRepository

    @Before
    fun setUp() {
        repository = mock()
    }

    // LoginUseCase
    @Test
    fun `LoginUseCase invoke calls repository logInWithEmail`() = runTest {
        val expected = ResponseState.Success(Unit)
        whenever(repository.logInWithEmail("user@test.com", "password123")).thenReturn(expected)

        val useCase = LoginUseCase(repository)
        val result = useCase("user@test.com", "password123")

        assertEquals(expected, result)
        verify(repository).logInWithEmail("user@test.com", "password123")
    }

    @Test
    fun `LoginUseCase returns Error on failure`() = runTest {
        val expected = ResponseState.Error("Invalid credentials")
        whenever(repository.logInWithEmail("bad@test.com", "wrong")).thenReturn(expected)

        val useCase = LoginUseCase(repository)
        val result = useCase("bad@test.com", "wrong")

        assertTrue(result is ResponseState.Error)
    }

    // SignUpUseCase
    @Test
    fun `SignUpUseCase invoke calls repository signUpWithEmail`() = runTest {
        val expected = ResponseState.Success(Unit)
        whenever(repository.signUpWithEmail("new@test.com", "pass123")).thenReturn(expected)

        val useCase = SignUpUseCase(repository)
        val result = useCase("new@test.com", "pass123")

        assertEquals(expected, result)
        verify(repository).signUpWithEmail("new@test.com", "pass123")
    }

    // SendForgotPasswordEmail
    @Test
    fun `SendForgotPasswordEmail invoke calls repository sendForgotPasswordEmail`() = runTest {
        val expected = ResponseState.Success(Unit)
        whenever(repository.sendForgotPasswordEmail("user@test.com")).thenReturn(expected)

        val useCase = SendForgotPasswordEmail(repository)
        val result = useCase("user@test.com")

        assertEquals(expected, result)
        verify(repository).sendForgotPasswordEmail("user@test.com")
    }

    // SendEmailVerification
    @Test
    fun `SendEmailVerification invoke calls repository sendEmailVerification`() = runTest {
        val expected = ResponseState.Success(Unit)
        whenever(repository.sendEmailVerification()).thenReturn(expected)

        val useCase = SendEmailVerification(repository)
        val result = useCase("any@test.com")

        assertEquals(expected, result)
        verify(repository).sendEmailVerification()
    }

    // SignInWithGoogleUseCase
    @Test
    fun `SignInWithGoogleUseCase returns Success from repository`() = runTest {
        val expected = ResponseState.Success(Unit)
        whenever(repository.signInWithGoogle()).thenReturn(expected)

        val useCase = SignInWithGoogleUseCase(repository)
        val result = useCase()

        assertEquals(expected, result)
        verify(repository).signInWithGoogle()
    }

    @Test
    fun `SignInWithGoogleUseCase returns Error when sign in fails`() = runTest {
        val expected = ResponseState.Error("Google sign in failed")
        whenever(repository.signInWithGoogle()).thenReturn(expected)

        val useCase = SignInWithGoogleUseCase(repository)
        val result = useCase()

        assertTrue(result is ResponseState.Error)
    }

    // SignOutUseCase
    @Test
    fun `SignOutUseCase invoke calls repository logout`() = runTest {
        val expected = ResponseState.Success(Unit)
        whenever(repository.logout()).thenReturn(expected)

        val useCase = SignOutUseCase(repository)
        val result = useCase()

        assertEquals(expected, result)
        verify(repository).logout()
    }

    // GetCurrentUserUseCase
    @Test
    fun `GetCurrentUserUseCase returns current user from repository`() = runTest {
        val mockUser = mock<FirebaseUser>()
        val expected = ResponseState.Success(mockUser)
        whenever(repository.getCurrentUser()).thenReturn(expected)

        val useCase = GetCurrentUserUseCase(repository)
        val result = useCase()

        assertTrue(result is ResponseState.Success)
        assertEquals(mockUser, (result as ResponseState.Success).data)
    }

    @Test
    fun `GetCurrentUserUseCase returns null user when not logged in`() = runTest {
        val expected = ResponseState.Success<FirebaseUser?>(null)
        whenever(repository.getCurrentUser()).thenReturn(expected)

        val useCase = GetCurrentUserUseCase(repository)
        val result = useCase()

        assertTrue(result is ResponseState.Success)
        assertNull((result as ResponseState.Success).data)
    }
}
