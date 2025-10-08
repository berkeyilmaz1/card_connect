package com.berkeyilmaz.cardapp.domain.usecase

import com.berkeyilmaz.cardapp.data.model.LoginRequest
import com.berkeyilmaz.cardapp.data.model.RegisterRequest
import com.berkeyilmaz.cardapp.domain.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(loginRequest: LoginRequest) = repository.login(loginRequest)
}

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(registerRequest: RegisterRequest) =
        repository.register(registerRequest)
}

//class RefreshUseCase @Inject constructor(
//    private val repository: AuthRepository
//) {
//    suspend operator fun invoke() = repository.refresh()
//}

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.logout()
}