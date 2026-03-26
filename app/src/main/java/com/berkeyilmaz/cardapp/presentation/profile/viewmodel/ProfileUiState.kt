package com.berkeyilmaz.cardapp.presentation.profile.viewmodel

enum class GreetingPeriod { MORNING, AFTERNOON, EVENING, NIGHT }

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val isUpdatingPassword: Boolean = false,
    val accountDeleted: Boolean = false,
    val passwordUpdateSuccess: Boolean = false,
    val displayName: String = "",
    val email: String = "",
    val phone: String = "",
    val photoBytes: ByteArray? = null,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProfileUiState) return false
        return isLoading == other.isLoading &&
            isSaving == other.isSaving &&
            isUploadingPhoto == other.isUploadingPhoto &&
            isDeletingAccount == other.isDeletingAccount &&
            isUpdatingPassword == other.isUpdatingPassword &&
            accountDeleted == other.accountDeleted &&
            passwordUpdateSuccess == other.passwordUpdateSuccess &&
            displayName == other.displayName &&
            email == other.email &&
            phone == other.phone &&
            photoBytes.contentEquals(other.photoBytes) &&
            saveSuccess == other.saveSuccess &&
            errorMessage == other.errorMessage
    }

    override fun hashCode(): Int {
        var result = isLoading.hashCode()
        result = 31 * result + isSaving.hashCode()
        result = 31 * result + isUploadingPhoto.hashCode()
        result = 31 * result + isDeletingAccount.hashCode()
        result = 31 * result + isUpdatingPassword.hashCode()
        result = 31 * result + accountDeleted.hashCode()
        result = 31 * result + passwordUpdateSuccess.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + (photoBytes?.contentHashCode() ?: 0)
        result = 31 * result + saveSuccess.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        return result
    }
}
