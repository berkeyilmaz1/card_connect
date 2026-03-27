package com.berkeyilmaz.cardapp.presentation.contact_detail.viewmodel

import com.berkeyilmaz.cardapp.domain.contact.model.Contact

sealed class ContactDetailUiState {
    data object Loading : ContactDetailUiState()
    data class Success(val contact: Contact) : ContactDetailUiState()
    data class Error(val message: String) : ContactDetailUiState()
}
