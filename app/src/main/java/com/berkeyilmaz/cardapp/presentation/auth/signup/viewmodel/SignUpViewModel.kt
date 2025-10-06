package com.berkeyilmaz.cardapp.presentation.auth.signup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignUpState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val isLoading: Boolean = false,
)

sealed class SignUpUiEvent {
    data class ShowError(val message: String) : SignUpUiEvent()
    data class Navigate(val route: String) : SignUpUiEvent()
}

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpState>(SignUpState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SignUpUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    fun signUp() {
        setLoading(true)
        val email = uiState.value.email
        val password = uiState.value.password
        if (email.isBlank() || password.isBlank()) {
            setLoading(false)
            viewModelScope.launch {
                _eventFlow.emit(SignUpUiEvent.ShowError("Email ve şifre boş olamaz"))
            }
            return
        }
        setLoading(false)
        viewModelScope.launch {
//            _eventFlow.emit(SignInUiEvent.Navigate("home"))
        }
    }

    fun changeEmail(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun changePassword(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun changeFullName(value: String) {
        _uiState.update { it.copy(fullName = value) }
    }

    fun changePhoneNumber(value: String) {
        _uiState.update { it.copy(phoneNumber = value) }
    }

    fun navigateToSignIn() {
        viewModelScope.launch {
            _eventFlow.emit(SignUpUiEvent.Navigate(Screen.SignIn.route))
        }
    }
}