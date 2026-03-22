package com.berkeyilmaz.cardapp.domain.user.usecase

import com.berkeyilmaz.cardapp.domain.user.UserRepository
import javax.inject.Inject

class UpdateDisplayNameUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(displayName: String) = repository.updateDisplayName(displayName)
}

class SavePhotoUrlUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String, photoUrl: String) =
        repository.savePhotoUrl(userId, photoUrl)
}

class GetPhotoUrlUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String) = repository.getPhotoUrl(userId)
}

class SaveUserInfoUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String, displayName: String, phone: String) =
        repository.saveUserInfo(userId, displayName, phone)
}

class GetUserInfoUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String) = repository.getUserInfo(userId)
}
