
package com.berkeyilmaz.cardapp.presentation.auth.signin.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.domain.auth.AuthResult
import com.berkeyilmaz.cardapp.domain.auth.usecase.LoginOrRegisterUseCase
import com.berkeyilmaz.cardapp.domain.auth.usecase.SignInWithGoogleUseCase
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
    private val loginOrRegisterUseCase: LoginOrRegisterUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SignInUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun signInOrRegister() {
        // Early return için validation
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
            val response = loginOrRegisterUseCase(email, password)

            withContext(Dispatchers.Main) {
                when (response) {
                    is AuthResult.Success -> {
                        // ✅ Main Graph'a yönlendir
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