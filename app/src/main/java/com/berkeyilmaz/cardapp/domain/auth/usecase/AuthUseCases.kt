package com.berkeyilmaz.cardapp.domain.auth.usecase

import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.berkeyilmaz.cardapp.domain.auth.AuthRepository
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        repository.logInWithEmail(email, password)
}

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        repository.signUpWithEmail(email, password)
}

class SendForgotPasswordEmail @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String) = repository.sendForgotPasswordEmail(email)
}

class SendEmailVerification @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String) = repository.sendEmailVerification()
}

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): ResponseState<Unit> {
        return repository.signInWithGoogle()
    }
}

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): ResponseState<Unit> {
        return authRepository.logout()
    }
}

class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): ResponseState<FirebaseUser?> =
        repository.getCurrentUser()
}

class WriteInitContactSyncDataUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(userId: String, syncValue: String): ResponseState<Unit> =
        repository.writeInitContactSyncData(userId, syncValue)
}

class ReadInitContactSyncDataUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(userId: String): ResponseState<String> =
        repository.readInitContactSyncData(userId)
}