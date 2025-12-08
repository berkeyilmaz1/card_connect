package com.berkeyilmaz.cardapp.presentation.main.contact.viewmodel

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import com.berkeyilmaz.cardapp.domain.contact.usecase.GetContactsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ContactUiState {
    data object Idle : ContactUiState()
    data class PermissionDenied(val isPermanentlyDenied: Boolean) : ContactUiState()
    data object Loading : ContactUiState()
    data class Success(val contacts: List<InternalContact>) : ContactUiState()
}

sealed class ContactUiEvent {
    data class ShowError(val message: String) : ContactUiEvent()
}

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val getContactsListUseCase: GetContactsListUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<ContactUiState>(ContactUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ContactUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var hasRequestedPermission = false

    fun checkAndRequestPermission(
        activityContext: Context,
        onRequestPermission: () -> Unit
    ) {
        val hasPermission = ContextCompat.checkSelfPermission(
            activityContext,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        when {
            // İzin zaten verilmiş, kişileri yükle
            hasPermission -> {
                viewModelScope.launch {
                    getContacts(activityContext.contentResolver)
                }
            }

            // İzin reddedilmiş ama tekrar istenebilir
            shouldShowRationale(activityContext) -> {
                _uiState.value = ContactUiState.PermissionDenied(isPermanentlyDenied = false)
                if (!hasRequestedPermission) {
                    hasRequestedPermission = true
                    onRequestPermission()
                }
            }

            // İlk kez soruluyor veya kalıcı olarak reddedilmiş
            else -> {
                if (!hasRequestedPermission) {
                    // İlk kez soruluyor
                    _uiState.value = ContactUiState.PermissionDenied(isPermanentlyDenied = false)
                    hasRequestedPermission = true
                    onRequestPermission()
                } else {
                    // Kalıcı olarak reddedilmiş (Don't ask again seçilmiş)
                    _uiState.value = ContactUiState.PermissionDenied(isPermanentlyDenied = true)
                }
            }
        }
    }

    fun onPermissionResult(
        activityContext: Context,
        isGranted: Boolean
    ) {
        when {
            isGranted -> {
                // İzin verildi, kişileri yükle
                viewModelScope.launch {
                    getContacts(activityContext.contentResolver)
                }
            }

            shouldShowRationale(activityContext) -> {
                // İzin reddedildi ama tekrar istenebilir
                _uiState.value = ContactUiState.PermissionDenied(isPermanentlyDenied = false)
            }

            else -> {
                // İzin kalıcı olarak reddedildi (Don't ask again)
                _uiState.value = ContactUiState.PermissionDenied(isPermanentlyDenied = true)
            }
        }
    }

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

    fun retryLoadContacts(contentResolver: ContentResolver) {
        viewModelScope.launch {
            getContacts(contentResolver)
        }
    }

    private fun shouldShowRationale(activityContext: Context): Boolean {
        val activity = activityContext as? android.app.Activity ?: return false
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.READ_CONTACTS
        )
    }

    fun resetPermissionState() {
        hasRequestedPermission = false
        _uiState.value = ContactUiState.Idle
    }
}