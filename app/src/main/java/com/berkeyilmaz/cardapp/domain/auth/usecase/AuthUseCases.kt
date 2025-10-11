package com.berkeyilmaz.cardapp.domain.auth.usecase

import com.berkeyilmaz.cardapp.data.model.User
import com.berkeyilmaz.cardapp.domain.auth.AuthRepository
import com.berkeyilmaz.cardapp.domain.auth.AuthResult
import javax.inject.Inject

class LoginOrRegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        repository.loginOrRegister(email, password)
}

class SendForgotPasswordEmail @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String) = repository.sendForgotPasswordEmail(email)
}

class SendEmailVerification constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String) = repository.sendEmailVerification()
}

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): AuthResult<Unit> {
        return repository.signInWithGoogle()
    }
}