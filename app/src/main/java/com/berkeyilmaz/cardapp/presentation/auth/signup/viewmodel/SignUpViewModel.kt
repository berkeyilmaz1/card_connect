package com.berkeyilmaz.cardapp.presentation.auth.signup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.data.model.RegisterRequest
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

data class SignUpState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val isLoading: Boolean = false,
)

sealed class SignUpUiEvent {
    data class ShowError(val message: String) : SignUpUiEvent()
    data class Navigate(val route: String, val email: String) : SignUpUiEvent() // todo change this after testing
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpState>(SignUpState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SignUpUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    fun signUp() {
        setLoading(true)
        val email = uiState.value.email
        val password = uiState.value.password
        if (!checkAllFieldsFilled()) {
            setLoading(false)
            viewModelScope.launch {
                _eventFlow.emit(SignUpUiEvent.ShowError("Please fill all fields"))
            }
            return
        }
        val userData = RegisterRequest(
            fullName = uiState.value.fullName,
            email = email,
            password = password,
            phoneNumber = uiState.value.phoneNumber
        )
        viewModelScope.launch(Dispatchers.IO) {
            val response = authRepository.register(userData)
            withContext(Dispatchers.Main) {
                when (response) {
                    is AuthResult.Success -> _eventFlow.emit(SignUpUiEvent.Navigate(Screen.Scan.route, email))
                    is AuthResult.Error -> _eventFlow.emit(SignUpUiEvent.ShowError(response.exception))
                }
            }
            setLoading(false)
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

    fun checkAllFieldsFilled(): Boolean {
        val state = uiState.value
        return state.fullName.isNotBlank() && state.email.isNotBlank() && state.password.isNotBlank() && state.phoneNumber.isNotBlank()
    }

    fun navigateToSignIn() {
        viewModelScope.launch {
            _eventFlow.emit(SignUpUiEvent.Navigate(Screen.SignIn.route,""))
        }
    }
}