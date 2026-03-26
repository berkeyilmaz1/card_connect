package com.berkeyilmaz.cardapp.domain.auth.usecase

import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.berkeyilmaz.cardapp.domain.auth.AuthRepository
import com.berkeyilmaz.cardapp.domain.user.UserRepository
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
    suspend operator fun invoke() = repository.sendEmailVerification()
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

class ReAuthenticateUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(password: String) = repository.reAuthenticate(password)
}

class UpdatePasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(newPassword: String) = repository.updatePassword(newPassword)
}

class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): ResponseState<Unit> {
        val userResult = authRepository.getCurrentUser()
        if (userResult !is ResponseState.Success || userResult.data == null) {
            return ResponseState.Error("No user signed in")
        }
        
        val uid = userResult.data.uid
        
        val deleteFirestoreResult = userRepository.deleteUserData(uid)
        if (deleteFirestoreResult is ResponseState.Error) {
            return deleteFirestoreResult
        }
        
        return authRepository.deleteAccount()
    }
}