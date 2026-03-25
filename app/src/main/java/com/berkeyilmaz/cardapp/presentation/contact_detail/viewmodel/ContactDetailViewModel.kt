package com.berkeyilmaz.cardapp.presentation.contact_detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.domain.contact.usecase.GetContactByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getContactByIdUseCase: GetContactByIdUseCase
) : ViewModel() {

    private val contactId: String =
        checkNotNull(savedStateHandle[Screen.Main.ContactDetail.ARG_ID])

    private val _uiState =
        MutableStateFlow<ContactDetailUiState>(ContactDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadContact()
    }

    private fun loadContact() {
        viewModelScope.launch {
            getContactByIdUseCase(contactId).fold(
                onSuccess = { _uiState.value = ContactDetailUiState.Success(it) },
                onFailure = { _uiState.value = ContactDetailUiState.Error(it.message ?: "Hata") }
            )
        }
    }
}
