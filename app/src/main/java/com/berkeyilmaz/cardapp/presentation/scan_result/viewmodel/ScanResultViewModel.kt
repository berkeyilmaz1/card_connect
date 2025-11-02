package com.berkeyilmaz.cardapp.presentation.scan_result.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ScanResultState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val fullName: String? = null,
    val jobTitle: String? = null,
    val company: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val address: String? = null,
    val notes: String? = null
)

@HiltViewModel
class ScanResultViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ScanResultState())
    val uiState = _uiState.asStateFlow()

    fun updateFullName(value: String) {
        _uiState.update { it.copy(fullName = value) }
    }

    fun updateJobTitle(value: String) {
        _uiState.update { it.copy(jobTitle = value) }
    }

    fun updateCompany(value: String) {
        _uiState.update { it.copy(company = value) }
    }

    fun updatePhoneNumber(value: String) {
        _uiState.update { it.copy(phoneNumber = value) }
    }

    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun updateAddress(value: String) {
        _uiState.update { it.copy(address = value) }
    }

    fun updateNotes(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

}