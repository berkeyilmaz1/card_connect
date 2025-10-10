package com.berkeyilmaz.cardapp.presentation.auth.signin.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.domain.auth.AuthResult
import com.berkeyilmaz.cardapp.domain.auth.usecase.LoginOrRegisterUseCase
import com.berkeyilmaz.cardapp.presentation.auth.signin.viewmodel.SignInUiEvent.Navigate
import com.berkeyilmaz.cardapp.presentation.auth.signin.viewmodel.SignInUiEvent.ShowError
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
    data class Navigate(val route: String) : SignInUiEvent()
}

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val loginOrRegisterUseCase: LoginOrRegisterUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignInState>(SignInState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SignInUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    fun signInOrRegister() {
        setLoading(true)
        val email = uiState.value.email
        val password = uiState.value.password
        if (email.isBlank() || password.isBlank()) {
            setLoading(false)
            viewModelScope.launch {
                _eventFlow.emit(ShowError(context.getString(R.string.fill_all_fields)))
            }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val response = loginOrRegisterUseCase(email, password)
            withContext(Dispatchers.Main) {
                when (response) {
                    is AuthResult.Success -> _eventFlow.emit(
                        Navigate(
                            Screen.Scan.route
                        )
                    )

                    is AuthResult.Error -> _eventFlow.emit(
                        ShowError(
                            response.message
                        )
                    )
                }
            }
            setLoading(false)
        }
    }

    fun navigateForgotPasswordView() {
        viewModelScope.launch {
            _eventFlow.emit(Navigate(Screen.ForgotPassword.route))
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
}