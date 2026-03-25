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
import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.berkeyilmaz.cardapp.data.local.InitSyncCacheRepositoryImpl
import com.berkeyilmaz.cardapp.domain.auth.usecase.GetCurrentUserUseCase
import com.berkeyilmaz.cardapp.domain.auth.usecase.ReadInitContactSyncDataUseCase
import com.berkeyilmaz.cardapp.domain.auth.usecase.WriteInitContactSyncDataUseCase
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import com.berkeyilmaz.cardapp.domain.contact.usecase.GetContactsListUseCase
import com.berkeyilmaz.cardapp.domain.contact.usecase.SaveInternalContactsListUseCase
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
}

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val getContactsListUseCase: GetContactsListUseCase,
    private val analyticsManager: AnalyticsManager,
    private val saveInternalContactsListUseCase: SaveInternalContactsListUseCase,
    private val writeInitContactSyncDataUseCase: WriteInitContactSyncDataUseCase,
    private val readInitContactSyncDataUseCase: ReadInitContactSyncDataUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val initSyncCache: InitSyncCacheRepositoryImpl,
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
            val contacts = getContactsListUseCase(contentResolver)
            Log.d("BerkeTag", "Contacts loaded: ${contacts.size}")
            performInitialSyncIfNeeded(contacts)
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

    private suspend fun performInitialSyncIfNeeded(contacts: List<InternalContact>) {
        val user = (getCurrentUserUseCase() as? ResponseState.Success)?.data ?: return

        if (initSyncCache.isInitSyncDone(user.uid)) return

        val syncValue = (readInitContactSyncDataUseCase(user.uid) as? ResponseState.Success)?.data
        if (syncValue == "true") {
            initSyncCache.markInitSyncDone(user.uid)
            return
        }

        val result = saveInternalContactsListUseCase(user.uid, contacts)
        when (result) {
            is ResponseState.Success<*> -> {
                writeInitContactSyncDataUseCase(user.uid, "true")
                initSyncCache.markInitSyncDone(user.uid)
            }
            is ResponseState.Error -> {
                _uiEvent.emit(ContactUiEvent.ShowError(result.message))
            }
        }
    }

    fun onBottomSheetDismissRequest() {
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