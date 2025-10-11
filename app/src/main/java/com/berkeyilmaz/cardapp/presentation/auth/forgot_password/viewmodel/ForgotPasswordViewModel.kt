package com.berkeyilmaz.cardapp.presentation.auth.forgot_password.viewmodel

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.domain.auth.AuthResult
import com.berkeyilmaz.cardapp.domain.auth.usecase.SendForgotPasswordEmail
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

data class ForgotPasswordState(
    val email: String = "", val isLoading: Boolean = false, val error: String? = null
)

sealed class ForgotPasswordUiEvent {
    data class Success(val message: String) : ForgotPasswordUiEvent()
    data class ShowError(val message: String) : ForgotPasswordUiEvent()
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: SendForgotPasswordEmail,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ForgotPasswordUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun sendForgotPasswordEmail() {
        setLoading(true)
        val email = uiState.value.email
        if (email.isBlank()) {
            setLoading(false)
            viewModelScope.launch {
                _eventFlow.emit(ForgotPasswordUiEvent.ShowError(context.getString(R.string.fill_all_fields)))
            }
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setLoading(false)
            viewModelScope.launch {
                _eventFlow.emit(ForgotPasswordUiEvent.ShowError(context.getString(R.string.valid_email_error)))
            }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val response = forgotPasswordUseCase(_uiState.value.email)
            withContext(Dispatchers.Main) {
                when (response) {
                    is AuthResult.Success -> _eventFlow.emit(
                        ForgotPasswordUiEvent.Success(
                            context.getString(R.string.email_sent_successfully)
                        )
                    )

                    is AuthResult.Error -> _eventFlow.emit(
                        ForgotPasswordUiEvent.ShowError(
                            response.message
                        )
                    )
                }
            }
            setLoading(false)
        }

    }


    fun changeEmail(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                error = if (Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches() || email.isBlank()
                ) null else context.getString(R.string.invalid_email)
            )
        }
    }


    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }
}