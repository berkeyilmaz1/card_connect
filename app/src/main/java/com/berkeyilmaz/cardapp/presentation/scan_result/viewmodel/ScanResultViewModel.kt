package com.berkeyilmaz.cardapp.presentation.scan_result.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.analytics.AnalyticsManager
import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.berkeyilmaz.cardapp.core.util.ContactsHelper
import com.berkeyilmaz.cardapp.domain.auth.usecase.GetCurrentUserUseCase
import com.berkeyilmaz.cardapp.domain.photo.model.Photo
import com.berkeyilmaz.cardapp.domain.photo.usecase.InsertPhotoUseCase
import com.berkeyilmaz.cardapp.domain.scan.usecase.CreateContactUseCase
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import com.berkeyilmaz.cardapp.domain.scan_result.model.SocialMedia
import com.berkeyilmaz.cardapp.domain.scan_result.model.Tag
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class ScanResultState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val fullName: String? = null,
    val jobTitle: String? = null,
    val company: String? = null,
    val phones: List<String> = emptyList(),
    val emails: List<String> = emptyList(),
    val addresses: String? = null,
    val websites: List<String> = emptyList(),
    val socialMedias: List<SocialMedia> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val notes: String? = null,
    val image: String? = null,
)

@HiltViewModel
class ScanResultViewModel @Inject constructor(
    private val createContactsUseCase: CreateContactUseCase,
    private val insertPhotoUseCase: InsertPhotoUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val analyticsManager: AnalyticsManager,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(ScanResultState())
    val uiState = _uiState.asStateFlow()

    fun createContact() {
        val currentState = _uiState.value

        // Basic validation: require a name or at least one contact method
        val hasName = !currentState.fullName.isNullOrBlank()
        val hasContactMethod =
            currentState.phones.isNotEmpty() || currentState.emails.isNotEmpty()
        if (!hasName && !hasContactMethod) {
            _uiState.update { it.copy(errorMessage = context.getString(R.string.scan_result_feedback_error)) }
            return
        }

        val contact = ContactRequest(
            fullName = currentState.fullName.orEmpty(),
            title = currentState.jobTitle.orEmpty(),
            organization = currentState.company.orEmpty(),
            phones = currentState.phones,
            emails = currentState.emails,
            websites = currentState.websites,
            address = currentState.addresses.orEmpty(),
            socialMedias = currentState.socialMedias,
            tags = currentState.tags,
            note = currentState.notes.orEmpty(),
            imageUrl = currentState.image.orEmpty()
        )

        Log.i("ScanResultViewModel", "Creating contact: $contact")

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // 1. Önce telefon rehberine kaydet, internal ID'yi al
                    val internalContactId = ContactsHelper.addContactToPhone(
                        context = context,
                        displayName = currentState.fullName,
                        phoneNumber = currentState.phones.firstOrNull(),
                        email = currentState.emails.firstOrNull(),
                        company = currentState.company,
                        jobTitle = currentState.jobTitle,
                        address = currentState.addresses,
                        website = currentState.websites.firstOrNull(),
                        notes = currentState.notes
                    )
                    Log.i("BerkeTag", "Phone contact saved, internalContactId=$internalContactId")

                    // 2. Internal ID'yi de taşıyarak Firebase'e kaydet
                    val createdContact = createContactsUseCase(
                        contact.copy(internalContactId = internalContactId.orEmpty())
                    ).getOrThrow()

                    val user = getCurrentUser()
                    if (user?.uid == null) {
                        throw Exception("User not authenticated")
                    }
                    val photo = Photo(
                        contactId = createdContact.contactId,
                        userId = user.uid,
                        filePath = currentState.image ?: "",
                        createdAt = System.currentTimeMillis()
                    )
                    Log.i("BerkeTag", "Inserting photo: $photo")
                    insertPhotoUseCase(photo)
                }
                analyticsManager.logScanCard()
                analyticsManager.logContactAdded()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSaved = true,
                        successMessage = context.getString(R.string.contact_saved_successfully)
                    )
                }

                deleteLocalImage(currentState.image)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun deleteLocalImage(imagePath: String?) {
        if (imagePath.isNullOrEmpty()) return

        try {
            val imageFile = java.io.File(imagePath)
            if (imageFile.exists()) {
                val deleted = imageFile.delete()
                if (deleted) {
                    Log.i("ScanResultViewModel", "Local image deleted: $imagePath")
                } else {
                    Log.w("ScanResultViewModel", "Failed to delete local image: $imagePath")
                }
            }
        } catch (e: Exception) {
            Log.e("ScanResultViewModel", "Error deleting local image: ${e.message}", e)
        }
    }


    fun updateFullName(value: String) {
        _uiState.update { it.copy(fullName = value) }
    }

    fun updateJobTitle(value: String) {
        _uiState.update { it.copy(jobTitle = value) }
    }

    fun updateCompany(value: String) {
        _uiState.update { it.copy(company = value) }
    }

    fun updatePhones(value: List<String>) {
        _uiState.update { it.copy(phones = value) }
    }

    fun updateEmails(value: List<String>) {
        _uiState.update { it.copy(emails = value) }
    }

    fun updateAddresses(value: String) {
        _uiState.update { it.copy(addresses = value) }
    }

    fun updateNotes(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun updateWebsites(value: List<String>) {
        _uiState.update { it.copy(websites = value) }
    }

    fun updateSocialMedia(value: List<SocialMedia>) {
        _uiState.update { it.copy(socialMedias = value) }
    }

    fun updateTags(value: List<Tag>) {
        _uiState.update { it.copy(tags = value) }
    }


    fun updateImage(value: String) {
        _uiState.update { it.copy(image = value) }
    }

    fun resetSavedState() {
        _uiState.update { it.copy(isSaved = false) }
    }

    suspend fun getCurrentUser(): FirebaseUser? {
        val result = getCurrentUserUseCase()
        return if (result is ResponseState.Success) {
            result.data
        } else {
            null
        }
    }
}