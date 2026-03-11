package com.berkeyilmaz.cardapp.presentation.main.contact.viewmodel

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.analytics.AnalyticsManager
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContactChanges
import com.berkeyilmaz.cardapp.domain.contact.usecase.GetContactsListUseCase
import com.berkeyilmaz.cardapp.domain.contact.usecase.SuggestTagsForContactsUseCase
import com.berkeyilmaz.cardapp.domain.scan.usecase.CreateContactUseCase
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class ContactUiState {
    data object Idle : ContactUiState()
    data class PermissionDenied(val isPermanentlyDenied: Boolean) : ContactUiState()
    data object Loading : ContactUiState()
    data class Success(val contacts: List<InternalContact>) : ContactUiState()
}

sealed class AnalyzeBottomSheetState {
    data object Hidden : AnalyzeBottomSheetState()
    data class Loading(val contactCount: Int) : AnalyzeBottomSheetState()
    data class Success(val suggestions: List<ContactRequest>) : AnalyzeBottomSheetState()
    data class Error(val message: String) : AnalyzeBottomSheetState()
}

sealed class ContactUiEvent {
    data class ShowError(val message: String) : ContactUiEvent()
    data object ShowCancelConfirmation : ContactUiEvent()
}

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val getContactsListUseCase: GetContactsListUseCase,
    private val suggestTagsForContactsUseCase: SuggestTagsForContactsUseCase,
    private val createContactUseCase: CreateContactUseCase,
    private val analyticsManager: AnalyticsManager,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<ContactUiState>(ContactUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _bottomSheetState =
        MutableStateFlow<AnalyzeBottomSheetState>(AnalyzeBottomSheetState.Hidden)
    val bottomSheetState = _bottomSheetState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ContactUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var hasRequestedPermission = false
    private var analyzeJob: Job? = null

    fun checkAndRequestPermission(
        activityContext: Context, onRequestPermission: () -> Unit
    ) {
        val hasPermission = ContextCompat.checkSelfPermission(
            activityContext, Manifest.permission.READ_CONTACTS
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
        activityContext: Context, isGranted: Boolean
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
            val (contacts, changes) = getContactsListUseCase(contentResolver)
            Log.d("BerkeTag", "Contacts loaded: ${contacts.size}")
            if (changes.hasChanges) {
                checkIfThereAreAnyAddedContacts(changes)
            }
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

    private fun checkIfThereAreAnyAddedContacts(changes: InternalContactChanges) {
        if (changes.added.isEmpty()) {
            return
        }
        Log.d("BerkeTag", "Added contacts: ${changes.added}")
        analyzeNewContacts(changes.added)
    }

    private fun analyzeNewContacts(addedContacts: List<InternalContact>) {
        analyzeJob = viewModelScope.launch {
            try {
                _bottomSheetState.value = AnalyzeBottomSheetState.Loading(addedContacts.size)

                val suggestions = suggestTagsForContactsUseCase(addedContacts)
                Log.d("BerkeTag", "Suggestions received: ${suggestions.size}")

                _bottomSheetState.value = AnalyzeBottomSheetState.Success(suggestions)
            } catch (e: Exception) {
                Log.e("BerkeTag", "Analyze error: ${e.message}")
                _bottomSheetState.value = AnalyzeBottomSheetState.Error(
                    e.localizedMessage
                        ?: context.getString(R.string.an_error_occurred_when_getting_contacts)
                )
            }
        }
    }

    fun onBottomSheetDismissRequest() {
        // Eğer loading durumundaysa uyarı göster
        if (_bottomSheetState.value is AnalyzeBottomSheetState.Loading) {
            viewModelScope.launch {
                _uiEvent.emit(ContactUiEvent.ShowCancelConfirmation)
            }
        } else {
            dismissBottomSheet()
        }
    }

    fun confirmCancelAnalysis() {
        analyzeJob?.cancel()
        analyzeJob = null
        dismissBottomSheet()
    }

    fun dismissBottomSheet() {
        _bottomSheetState.value = AnalyzeBottomSheetState.Hidden
    }

    fun retryLoadContacts(contentResolver: ContentResolver) {
        viewModelScope.launch {
            getContacts(contentResolver)
        }
    }

    private fun shouldShowRationale(activityContext: Context): Boolean {
        val activity = activityContext as? android.app.Activity ?: return false
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity, Manifest.permission.READ_CONTACTS
        )
    }

    fun resetPermissionState() {
        hasRequestedPermission = false
        _uiState.value = ContactUiState.Idle
    }

    fun onContactsApproved(contacts: List<ContactRequest>) {
        viewModelScope.launch {
            try {
                // Batch ile kaydet
                createContactsBatch(contacts)
                repeat(contacts.size) { analyticsManager.logContactAdded() }
            } catch (e: Exception) {
                Log.e("ContactViewModel", "Batch createContact error: ${e.message}")
            }
            dismissBottomSheet()
        }
    }

    private suspend fun createContactsBatch(contacts: List<ContactRequest>) {
        // Firebase Firestore batch işlemi
        // Firestore referansını alın
        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val batch = firestore.batch()
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
            ?: throw Exception("User not authenticated")
        val userContactsRef = firestore.collection("users").document(userId).collection("contacts")
        for (contact in contacts) {
            val newDoc = userContactsRef.document()
            batch.set(newDoc, contact)
        }
        batch.commit().await()
    }
}