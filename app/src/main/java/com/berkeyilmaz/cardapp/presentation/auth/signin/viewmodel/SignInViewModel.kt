package com.berkeyilmaz.cardapp.presentation.auth.signin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.data.model.LoginRequest
import com.berkeyilmaz.cardapp.domain.AuthRepository
import com.berkeyilmaz.cardapp.domain.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
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
    data class Navigate(val route: String, val email: String) :
        SignInUiEvent()// todo change this after testing
}

@HiltViewModel
class SignInViewModel @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow<SignInState>(SignInState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SignInUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun changeEmail(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun changePassword(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun signIn() {
        setLoading(true)
        val email = uiState.value.email
        val password = uiState.value.password
        if (email.isBlank() || password.isBlank()) {
            setLoading(false)
            viewModelScope.launch {
                _eventFlow.emit(SignInUiEvent.ShowError("Please fill all fields"))
            }
            return
        }
        val userData = LoginRequest(
            identifier = email, password = password
        )
        viewModelScope.launch(Dispatchers.IO) {
            val response = authRepository.login(userData)
            withContext(Dispatchers.Main) {
                when (response) {
                    is AuthResult.Success -> _eventFlow.emit(
                        SignInUiEvent.Navigate(
                            Screen.Scan.route, email
                        )
                    )

                    is AuthResult.Error -> _eventFlow.emit(SignInUiEvent.ShowError(response.exception))
                }
            }
            setLoading(false)
        }
    }

    fun navigateToSignUp() {
        viewModelScope.launch {
            _eventFlow.emit(SignInUiEvent.Navigate(Screen.SignUp.route, uiState.value.email))
        }
    }
}