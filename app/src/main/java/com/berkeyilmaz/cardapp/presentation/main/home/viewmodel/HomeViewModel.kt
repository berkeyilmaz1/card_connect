package com.berkeyilmaz.cardapp.presentation.main.home.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.contact.model.DuplicateContactGroup
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import com.berkeyilmaz.cardapp.domain.contact.usecase.FindDuplicateContactsUseCase
import com.berkeyilmaz.cardapp.domain.contact.usecase.GetRemoteContactsUseCase
import com.berkeyilmaz.cardapp.domain.contact.usecase.MergeContactsUseCase
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import com.berkeyilmaz.cardapp.presentation.main.home.widgets.PrimarySelection
import com.berkeyilmaz.cardapp.domain.home.usecase.GetCurrentUserUseCase
import com.berkeyilmaz.cardapp.domain.photo.model.Photo
import com.berkeyilmaz.cardapp.domain.photo.usecase.GetAllPhotosUseCase
import com.berkeyilmaz.cardapp.presentation.main.home.QuickActionOption
import com.berkeyilmaz.cardapp.presentation.main.home.models.HomeNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DuplicateBottomSheetState {
    data object Hidden : DuplicateBottomSheetState()
    data object Scanning : DuplicateBottomSheetState()
    data class Found(val groups: List<DuplicateContactGroup>, val currentIndex: Int = 0) :
        DuplicateBottomSheetState()
    data class Error(val message: String) : DuplicateBottomSheetState()
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val userName: String? = null,
    val userProfileImageUrl: String? = null,
    val notificationList: List<HomeNotification> = emptyList(),
    val recentlyScannedCards: List<Any> = emptyList(), // Replace with actual data model
    val snackbarMessage: String? = null,
    val contacts: List<Contact>? = null,
    val photos: List<Photo>? = null,
    val duplicateBottomSheetState: DuplicateBottomSheetState = DuplicateBottomSheetState.Hidden,
)

sealed class HomeInUiEvent {
    data class ShowError(val message: String) : HomeInUiEvent()
    data class Navigate(val route: String) : HomeInUiEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val getPhotosUseCase: GetAllPhotosUseCase,
    private val getRemoteContacts: GetRemoteContactsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val findDuplicateContactsUseCase: FindDuplicateContactsUseCase,
    private val mergeContactsUseCase: MergeContactsUseCase,
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<HomeInUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val quickActionOptions = listOf(
        QuickActionOption(
            title = context.getString(R.string.add_new),
            icon = Icons.Outlined.Add,
            route = Screen.Main.Scan.route
        ), QuickActionOption(
            title = context.getString(R.string.history), icon = Icons.Outlined.History, route = ""
        ), QuickActionOption(
            title = context.getString(R.string.favorites),
            icon = Icons.Outlined.StarOutline,
            route = "{}"
        ), QuickActionOption(
            title = context.getString(R.string.settings),
            icon = Icons.Outlined.Settings,
            route = Screen.Main.Settings.route
        )
    )

    init {
        fetchContacts()
        fetchPhotos()
    }

    fun calculateRecentlyScannedCards() {
        android.util.Log.d("BerkeTag", "calculateRecentlyScannedCards CALLED")
        android.util.Log.d("BerkeTag", "Photos count: ${uiState.value.photos?.size}, Contacts count: ${uiState.value.contacts?.size}")

        if (uiState.value.photos == null && uiState.value.contacts == null) {
            android.util.Log.d("BerkeTag", "Both photos and contacts are NULL - RETURNING")
            return
        }

        val contacts = uiState.value.contacts ?: emptyList()
        val photos = uiState.value.photos ?: emptyList()

        android.util.Log.d("BerkeTag", "Processing ${photos.size} photos and ${contacts.size} contacts")

        // Photo'ları tarihine göre sırala, son 4'ünü al ve ilgili contact'larla eşleştir
        val recentlyScannedCards = photos
            .sortedByDescending { it.createdAt }
            .take(5)
            .mapNotNull { photo ->
                val matchedContact = contacts.find { it.contactId == photo.contactId }
                android.util.Log.d("BerkeTag", "Photo ID: ${photo.id}, ContactID: ${photo.contactId} -> Matched: ${matchedContact?.fullName ?: "NO MATCH"}")
                matchedContact?.let {
                    Pair(it, photo)
                }
            }

        android.util.Log.d("BerkeTag", "Recently scanned cards FINAL COUNT: ${recentlyScannedCards.size}")
        recentlyScannedCards.forEach { (contact, photo) ->
            android.util.Log.d("BerkeTag", "Card: ${contact.fullName} - Photo: ${photo.id}")
        }

        _uiState.update {
            it.copy(recentlyScannedCards = recentlyScannedCards)
        }
    }


fun fetchContacts() {
    viewModelScope.launch {
        try {
            setLoading(true)

            val result = getRemoteContacts()

            val contacts = result.getOrNull()
            if (result.isFailure || contacts.isNullOrEmpty()) {
                updateErrorState("Contact list is empty")
                return@launch
            }
            _uiState.update { it.copy(contacts = contacts) }
            calculateRecentlyScannedCards()
            checkForDuplicates(contacts)
        } catch (e: Exception) {
            updateErrorState(context.getString(R.string.an_error_occurred))
        } finally {
            setLoading(false)
        }
    }

}

private fun checkForDuplicates(contacts: List<Contact>) {
    viewModelScope.launch {
        _uiState.update { it.copy(duplicateBottomSheetState = DuplicateBottomSheetState.Scanning) }
        try {
            val internalContacts = try {
                contactRepository.getInternalContacts(context.contentResolver)
            } catch (e: Exception) {
                Log.w("BerkeTag", "checkForDuplicates: could not fetch internal contacts: ${e.message}")
                emptyList()
            }
            val groups = findDuplicateContactsUseCase(contacts, internalContacts)
            _uiState.update {
                it.copy(
                    duplicateBottomSheetState = if (groups.isEmpty()) {
                        DuplicateBottomSheetState.Hidden
                    } else {
                        DuplicateBottomSheetState.Found(groups)
                    }
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    duplicateBottomSheetState = DuplicateBottomSheetState.Error(
                        e.message ?: context.getString(R.string.an_error_occurred)
                    )
                )
            }
        }
    }
}

fun onMergeApproved(group: DuplicateContactGroup, primarySelection: PrimarySelection) {
    viewModelScope.launch {
        when (primarySelection) {
            is PrimarySelection.Remote -> {
                val primary = primarySelection.contact
                val duplicates = group.contacts.filter { it.contactId != primary.contactId }
                val internalDuplicates = group.internalContacts
                mergeContactsUseCase(context.contentResolver, primary, duplicates, internalDuplicates)
            }
            is PrimarySelection.Internal -> {
                val internalPrimary = primarySelection.contact
                // InternalContact'tan ContactRequest oluştur
                val contactRequest = ContactRequest(
                    fullName = internalPrimary.fullName,
                    title = internalPrimary.title ?: "",
                    organization = internalPrimary.organization ?: "",
                    phones = internalPrimary.phoneNumbers,
                    emails = internalPrimary.emails ?: emptyList(),
                    websites = internalPrimary.websites ?: emptyList(),
                    address = internalPrimary.address ?: "",
                    note = internalPrimary.note ?: ""
                )
                // Firebase'e yeni contact oluştur — bu primary olacak
                val createResult = contactRepository.createContact(contactRequest)
                val newPrimary = createResult.getOrNull()
                if (newPrimary != null) {
                    // Tüm remote'lar duplicate, internal'dan sadece seçilen korunacak (diğerleri silinecek)
                    val internalDuplicates = group.internalContacts.filter {
                        it.contactId != internalPrimary.contactId
                    }
                    mergeContactsUseCase(
                        context.contentResolver,
                        newPrimary,
                        group.contacts, // tüm remote'lar silinecek (newPrimary ayrı oluşturuldu)
                        internalDuplicates
                    )
                }
            }
        }
        advanceToNextGroup()
        fetchContacts()
    }
}

fun onDuplicateGroupSkipped() {
    advanceToNextGroup()
}

fun dismissDuplicateBottomSheet() {
    _uiState.update { it.copy(duplicateBottomSheetState = DuplicateBottomSheetState.Hidden) }
}

private fun advanceToNextGroup() {
    val current = _uiState.value.duplicateBottomSheetState
    if (current is DuplicateBottomSheetState.Found) {
        val nextIndex = current.currentIndex + 1
        _uiState.update {
            it.copy(
                duplicateBottomSheetState = if (nextIndex < current.groups.size) {
                    current.copy(currentIndex = nextIndex)
                } else {
                    DuplicateBottomSheetState.Hidden
                }
            )
        }
    }
}

fun fetchPhotos() {
    viewModelScope.launch {
        try {
            setLoading(true)
            val userResponse = getCurrentUserUseCase()
            if (userResponse is ResponseState.Error) {
                updateErrorState(context.getString(R.string.failed_to_load_user_data))
                return@launch
            }
            val user = (userResponse as ResponseState.Success).data
            if (user == null) {
                updateErrorState(context.getString(R.string.failed_to_load_user_data))
                return@launch
            }

            getPhotosUseCase(user.uid).collect { photoList ->
                _uiState.update {
                    it.copy(
                        photos = photoList
                    )
                }
                calculateRecentlyScannedCards()
            }
        } catch (e: Exception) {
            updateErrorState(context.getString(R.string.an_error_occurred))
        } finally {
            setLoading(false)
        }
    }
}

//    suspend fun checkUserIsVerified() {
//        val isVerified = getCurrentUserUseCase().let { result ->
//            if (result is ResponseState.Success) {
//                Log.d("BerkeTag", "checkUserIsVerified result: ${result.data?.isEmailVerified}")
//                result.data?.isEmailVerified ?: false
//            } else {
//                false
//            }
//        }
//
//        if (!isVerified) {
//            addNotification(
//                HomeNotification(
//                    id = NotificationID.EMAIL_VERIFICATION,
//                    title = context.getString(R.string.notification_email_verification_title),
//                    subtitle = context.getString(R.string.notification_email_verification_subtitle),
//                    buttonText = context.getString(R.string.notification_email_verification_button),
//                    onButtonClick = {})
//            )
//        }
//    }
//
//    suspend fun checkUserProfileComplete() {
//        val isProfileComplete = getCurrentUserUseCase().let { result ->
//            Log.d("BerkeTag", "checkUserProfileComplete result: $result")
//            if (result is ResponseState.Success) {
//                val user = result.data
//                !user?.displayName.isNullOrEmpty() && !user.phoneNumber.isNullOrEmpty()// todo: check other profile fields when added
//            } else {
//                false
//            }
//        }
//        if (!isProfileComplete) {
//            addNotification(
//                HomeNotification(
//                    id = NotificationID.PROFILE_INCOMPLETE,
//                    title = context.getString(R.string.notification_profile_incomplete_title),
//                    subtitle = context.getString(R.string.notification_profile_incomplete_subtitle),
//                    buttonText = context.getString(R.string.notification_profile_incomplete_button),
//                    onButtonClick = {})
//            )
//        }
//    }
//
//    suspend fun getCurrentUser() {
//        setLoading(true)
//        val result = getCurrentUserUseCase()
//        if (result is ResponseState.Error) return updateErrorState(
//            context.getString(R.string.failed_to_load_user_data)
//        )
//        val user = (result as ResponseState.Success).data
//        try {
//            Log.d(
//                "BerkeTag",
//                "User data loaded name: ${user?.displayName}, photoUrl: ${user?.photoUrl}"
//            )
//            updateUserState(
//                user?.displayName ?: "", user?.photoUrl?.toString() ?: ""
//            )
//        } catch (_: Exception) {
//            updateErrorState(context.getString(R.string.failed_to_load_user_data))
//        } finally {
//            setLoading(false)
//        }
//    }

fun addNotification(notification: HomeNotification) {
    _uiState.update {
        val updatedList = it.notificationList.toMutableList().apply {
            add(notification)
        }
        it.copy(notificationList = updatedList)
    }
}

fun removeNotification(notification: HomeNotification) {
    _uiState.update {
        it.copy(notificationList = it.notificationList.filter { n -> n.id != notification.id })
    }
}

fun showSnackbar(message: String) {
    _uiState.update { it.copy(snackbarMessage = message) }
}

fun clearSnackbar() {
    _uiState.update { it.copy(snackbarMessage = null) }
}

private fun updateUserState(name: String, profileImageUrl: String) {
    _uiState.update {
        it.copy(
            userName = name, userProfileImageUrl = profileImageUrl
        )
    }
}

private fun updateErrorState(error: String) {
    viewModelScope.launch { _uiEvent.emit(HomeInUiEvent.ShowError(error)) }
}

private fun setLoading(isLoading: Boolean) {
    _uiState.update {
        it.copy(isLoading = isLoading)
    }
}
}