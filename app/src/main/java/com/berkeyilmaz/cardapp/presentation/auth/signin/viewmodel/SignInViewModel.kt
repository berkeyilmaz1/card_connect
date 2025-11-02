package com.berkeyilmaz.cardapp.presentation.auth.signin.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.domain.auth.AuthResult
import com.berkeyilmaz.cardapp.domain.auth.usecase.GetCurrentUserUseCase
import com.berkeyilmaz.cardapp.domain.auth.usecase.LoginUseCase
import com.berkeyilmaz.cardapp.domain.auth.usecase.SignInWithGoogleUseCase
import com.berkeyilmaz.cardapp.domain.auth.usecase.SignUpUseCase
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class SignInState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
)

sealed class SignInUiEvent {
    data class ShowError(val message: String) : SignInUiEvent()
    data object NavigateToMain : SignInUiEvent()
    data object NavigateToForgotPassword : SignInUiEvent()
}

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SignInUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun login() {
        val email = uiState.value.email
        val password = uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(
                    SignInUiEvent.ShowError(
                        context.getString(R.string.fill_all_fields)
                    )
                )
            }
            return
        }

        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = loginUseCase(email, password)

            withContext(Dispatchers.Main) {
                when (response) {
                    is AuthResult.Success -> {
                        // ✅ Main Graph'a yönlendir
                        val isUserVerified = checkUserIsVerified()
                        if (!isUserVerified) {
                            _eventFlow.emit(
                                SignInUiEvent.ShowError(
                                    context.getString(R.string.please_verify_your_email_address_before_signing_in)
                                )
                            )
                            setLoading(false)
                            return@withContext
                        }
                        _eventFlow.emit(SignInUiEvent.NavigateToMain)
                    }

                    is AuthResult.Error -> {
                        _eventFlow.emit(SignInUiEvent.ShowError(response.message))
                    }
                }
                setLoading(false)
            }
        }
    }

    fun signUp() {
        val email = uiState.value.email
        val password = uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(
                    SignInUiEvent.ShowError(
                        context.getString(R.string.fill_all_fields)
                    )
                )
            }
            return
        }

        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = signUpUseCase(email, password)

            withContext(Dispatchers.Main) {
                when (response) {
                    is AuthResult.Success -> {
                        // ✅ Main Graph'a yönlendir
                        val isUserVerified = checkUserIsVerified()
                        if (!isUserVerified) {
                            _eventFlow.emit(
                                SignInUiEvent.ShowError(
                                    context.getString(R.string.please_verify)
                                )
                            )
                            setLoading(false)
                            return@withContext
                        }
                        _eventFlow.emit(SignInUiEvent.NavigateToMain)
                    }

                    is AuthResult.Error -> {
                        _eventFlow.emit(SignInUiEvent.ShowError(response.message))
                    }
                }
                setLoading(false)
            }
        }
    }

    suspend fun checkUserIsVerified(): Boolean {
        val user = getCurrentUserUseCase()
        return if (user is AuthResult.Success) {
            user.data?.isEmailVerified == true
        } else {
            false
        }
    }

    suspend fun getCurrentUser(): FirebaseUser? {
        val result = getCurrentUserUseCase()
        return if (result is AuthResult.Success) {
            result.data
        } else {
            null
        }
    }

    suspend fun signInWithGoogle() {
        setLoading(true)
        val result = signInWithGoogleUseCase()

        when (result) {
            is AuthResult.Error -> {
                _eventFlow.emit(SignInUiEvent.ShowError(result.message))
            }

            is AuthResult.Success<*> -> {
                _eventFlow.emit(SignInUiEvent.NavigateToMain)
            }
        }
        setLoading(false)
    }

    fun navigateForgotPasswordView() {
        viewModelScope.launch {
            _eventFlow.emit(SignInUiEvent.NavigateToForgotPassword)
        }
    }

    fun changeEmail(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun changePassword(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    private fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }
}