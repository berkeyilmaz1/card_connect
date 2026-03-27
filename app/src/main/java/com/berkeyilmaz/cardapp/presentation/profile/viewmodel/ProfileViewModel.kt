package com.berkeyilmaz.cardapp.presentation.profile.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.berkeyilmaz.cardapp.domain.auth.usecase.DeleteAccountUseCase
import com.berkeyilmaz.cardapp.domain.auth.usecase.GetCurrentUserUseCase
import com.berkeyilmaz.cardapp.domain.auth.usecase.ReAuthenticateUseCase
import com.berkeyilmaz.cardapp.domain.auth.usecase.UpdatePasswordUseCase
import com.berkeyilmaz.cardapp.domain.user.usecase.GetPhotoUrlUseCase
import com.berkeyilmaz.cardapp.domain.user.usecase.GetUserInfoUseCase
import com.berkeyilmaz.cardapp.domain.user.usecase.SavePhotoUrlUseCase
import com.berkeyilmaz.cardapp.domain.user.usecase.SaveUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val saveUserInfoUseCase: SaveUserInfoUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val savePhotoUrlUseCase: SavePhotoUrlUseCase,
    private val getPhotoUrlUseCase: GetPhotoUrlUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val reAuthenticateUseCase: ReAuthenticateUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userResult = getCurrentUserUseCase()
            if (userResult is ResponseState.Success) {
                val user = userResult.data
                val email = user?.email ?: ""
                _uiState.update { it.copy(email = email) }

                val uid = user?.uid
                if (uid != null) {
                    val infoResult = getUserInfoUseCase(uid)
                    if (infoResult is ResponseState.Success) {
                        _uiState.update {
                            it.copy(
                                displayName = infoResult.data.displayName,
                                phone = infoResult.data.phone
                            )
                        }
                    }

                    val photoResult = getPhotoUrlUseCase(uid)
                    if (photoResult is ResponseState.Success) {
                        val raw = photoResult.data
                        val bytes = raw?.let { Base64.getDecoder().decode(it) }
                        _uiState.update { it.copy(photoBytes = bytes) }
                    }
                }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onDisplayNameChange(value: String) {
        _uiState.update { it.copy(displayName = value, saveSuccess = false) }
    }

    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phone = value, saveSuccess = false) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, saveSuccess = false) }
            val userResult = getCurrentUserUseCase()
            if (userResult !is ResponseState.Success || userResult.data == null) {
                _uiState.update { it.copy(isSaving = false, errorMessage = "No user signed in") }
                return@launch
            }
            val uid = userResult.data.uid
            val state = _uiState.value
            val result = saveUserInfoUseCase(uid, state.displayName, state.phone)
            when (result) {
                is ResponseState.Success -> _uiState.update {
                    it.copy(isSaving = false, saveSuccess = true)
                }
                is ResponseState.Error -> _uiState.update {
                    it.copy(isSaving = false, errorMessage = result.message)
                }
            }
        }
    }

    fun deleteAccount(password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAccount = true, errorMessage = null) }
            
            // 1. Re-authenticate
            val authResult = reAuthenticateUseCase(password)
            if (authResult is ResponseState.Error) {
                _uiState.update { it.copy(isDeletingAccount = false, errorMessage = authResult.message) }
                return@launch
            }
            
            // 2. Delete account and data
            when (val result = deleteAccountUseCase()) {
                is ResponseState.Success -> {
                    _uiState.update { it.copy(isDeletingAccount = false, accountDeleted = true) }
                }
                is ResponseState.Error -> {
                    _uiState.update { it.copy(isDeletingAccount = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingPassword = true, errorMessage = null, passwordUpdateSuccess = false) }
            
            // 1. Re-authenticate
            val authResult = reAuthenticateUseCase(currentPassword)
            if (authResult is ResponseState.Error) {
                _uiState.update { it.copy(isUpdatingPassword = false, errorMessage = authResult.message) }
                return@launch
            }
            
            // 2. Update password
            when (val result = updatePasswordUseCase(newPassword)) {
                is ResponseState.Success -> {
                    _uiState.update { it.copy(isUpdatingPassword = false, passwordUpdateSuccess = true) }
                }
                is ResponseState.Error -> {
                    _uiState.update { it.copy(isUpdatingPassword = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun onPhotoSelected(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingPhoto = true, errorMessage = null) }
            try {
                val userResult = getCurrentUserUseCase()
                if (userResult !is ResponseState.Success || userResult.data == null) {
                    _uiState.update {
                        it.copy(isUploadingPhoto = false, errorMessage = "No user signed in")
                    }
                    return@launch
                }
                val uid = userResult.data.uid
                val imageBytes: ByteArray? = withContext(Dispatchers.IO) {
                    compressImage(uri)
                }
                if (imageBytes == null) {
                    _uiState.update {
                        it.copy(isUploadingPhoto = false, errorMessage = "Could not read image")
                    }
                    return@launch
                }

                val base64 = Base64.getEncoder().encodeToString(imageBytes)
                when (val saveResult = savePhotoUrlUseCase(uid, base64)) {
                    is ResponseState.Success ->
                        _uiState.update {
                            it.copy(photoBytes = imageBytes, isUploadingPhoto = false)
                        }
                    is ResponseState.Error ->
                        _uiState.update {
                            it.copy(isUploadingPhoto = false, errorMessage = saveResult.message)
                        }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isUploadingPhoto = false, errorMessage = e.message ?: "Upload failed")
                }
            }
        }
    }

    private fun compressImage(uri: Uri): ByteArray? {
        val bytes = context.contentResolver.openInputStream(uri)?.readBytes() ?: return null
        val original = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null

        val maxDimension = 512
        val scaled = if (original.width > maxDimension || original.height > maxDimension) {
            val ratio = original.width.toFloat() / original.height.toFloat()
            val (w, h) = if (ratio > 1f) maxDimension to (maxDimension / ratio).toInt()
                         else (maxDimension * ratio).toInt() to maxDimension
            Bitmap.createScaledBitmap(original, w, h, true)
        } else original

        val out = ByteArrayOutputStream()
        var quality = 85
        scaled.compress(Bitmap.CompressFormat.JPEG, quality, out)

        while (out.size() > 1_000_000 && quality > 20) {
            out.reset()
            quality -= 10
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }

        if (scaled !== original) {
            original.recycle()
            scaled.recycle()
        }

        return out.toByteArray()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    fun resetPasswordUpdateSuccess() {
        _uiState.update { it.copy(passwordUpdateSuccess = false) }
    }
}
