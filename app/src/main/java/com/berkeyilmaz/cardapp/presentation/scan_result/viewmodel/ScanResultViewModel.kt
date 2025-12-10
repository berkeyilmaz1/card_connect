package com.berkeyilmaz.cardapp.presentation.scan_result.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.util.ContactsHelper
import com.berkeyilmaz.cardapp.domain.photo.model.Photo
import com.berkeyilmaz.cardapp.domain.photo.usecase.InsertPhotoUseCase
import com.berkeyilmaz.cardapp.domain.scan.usecase.CreateContactUseCase
import com.berkeyilmaz.cardapp.domain.scan_result.model.ConfirmedData
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import com.berkeyilmaz.cardapp.domain.scan_result.model.SocialMedia
import com.berkeyilmaz.cardapp.domain.scan_result.model.Tag
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
    val phoneNumber: String? = null,
    val email: String? = null,
    val address: String? = null,
    val websites: String? = null,
    val socialMedia: List<SocialMedia>? = null,
    val tags: List<Tag>? = listOf(),
    val notes: String? = null,
    val image: String? = null,
    val rawText: String? = null
)

@HiltViewModel
class ScanResultViewModel @Inject constructor(
    private val createContactsUseCase: CreateContactUseCase,
    private val insertPhotoUseCase: InsertPhotoUseCase,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(ScanResultState())
    val uiState = _uiState.asStateFlow()

    fun createContact() {
        val currentState = _uiState.value

        // Basic validation: require a name or at least one contact method
        val hasName = !currentState.fullName.isNullOrBlank()
        val hasContactMethod =
            !currentState.phoneNumber.isNullOrBlank() || !currentState.email.isNullOrBlank()
        if (!hasName && !hasContactMethod) {
            _uiState.update { it.copy(errorMessage = context.getString(R.string.scan_result_feedback_error)) }
            return
        }

        val contact = ContactRequest(
            imageUrl = "https://example.com/image.jpg",
            rawText = currentState.rawText,
            note = currentState.notes,
            confirmedData = ConfirmedData(
                fullName = currentState.fullName,
                title = currentState.jobTitle,
                organization = currentState.company,
                phones = currentState.phoneNumber?.let { listOf(it) } ?: emptyList(),
                emails = currentState.email?.let { listOf(it) } ?: emptyList(),
                addresses = currentState.address?.let { listOf(it) } ?: emptyList(),
                websites = currentState.websites?.let { listOf(it) } ?: emptyList(),
                socialMedia = currentState.socialMedia ?: emptyList(),
                tags = currentState.tags ?: emptyList()))

//TODO: ENESE SÖYLE CREATE CONTACT YAPARKEN GERİYE OLUŞTURULAN CONTACT'I DÖNSÜN PHOTO KAYDEDERKEN NASIL OLACAK

        val photo = Photo(
            contactId = "", // TODO: Set the actual contact ID after creation
            userId = "",    // TODO:getcurrentuserId
            filePath = "" // TODO: Set path logic
        )

        Log.i("ScanResultViewModel", "Creating contact: $contact")

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    createContactsUseCase(contact)
                    insertPhotoUseCase(photo)

                    // Telefon rehberine ekle
                    ContactsHelper.addContactToPhone(
                        context = context,
                        displayName = currentState.fullName,
                        phoneNumber = currentState.phoneNumber,
                        email = currentState.email,
                        company = currentState.company,
                        jobTitle = currentState.jobTitle,
                        address = currentState.address,
                        website = currentState.websites,
                        notes = currentState.notes
                    )
                }
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

    // Accept single string from text fields and convert to list internally.
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

    fun updateWebsites(value: String) {
        _uiState.update { it.copy(websites = value) }
    }

    fun updateSocialMedia(value: List<SocialMedia>) {
        _uiState.update { it.copy(socialMedia = value) }
    }

    fun updateTags(value: List<Tag>) {
        _uiState.update { it.copy(tags = value) }
    }


    fun updateImage(value: String) {
        _uiState.update { it.copy(image = value) }
    }

    fun updateRawText(value: String) {
        _uiState.update { it.copy(rawText = value) }
    }

    fun resetSavedState() {
        _uiState.update { it.copy(isSaved = false) }
    }

}