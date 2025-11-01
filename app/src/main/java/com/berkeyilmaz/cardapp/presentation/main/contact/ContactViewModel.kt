package com.berkeyilmaz.cardapp.presentation.main.contact

import android.content.ContentResolver
import android.content.Context
import androidx.lifecycle.ViewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.contact.usecase.GetContactsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


sealed class ContactUiState {
    data object Idle : ContactUiState()
    data object Loading : ContactUiState()
    data class Success(val contacts: List<Contact>) : ContactUiState()
}

sealed class ContactUiEvent {
    data class ShowError(val message: String) : ContactUiEvent()
}

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val getContactsListUseCase: GetContactsListUseCase,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow<ContactUiState>(ContactUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ContactUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    suspend fun getContacts(contentResolver: ContentResolver) {
        try {
            _uiState.value = ContactUiState.Loading
            val contacts = getContactsListUseCase(contentResolver)
            _uiState.value = ContactUiState.Success(contacts)
        } catch (e: Exception) {
            _uiEvent.emit(
                ContactUiEvent.ShowError(
                    e.localizedMessage
                        ?: context.getString(R.string.an_error_occurred_when_getting_contacts)
                )
            )
            _uiState.value = ContactUiState.Idle
        }
    }

    private fun updateContacts(contacts: List<Contact>) {
        _uiState.update {
            ContactUiState.Success(contacts)
        }
    }
}

