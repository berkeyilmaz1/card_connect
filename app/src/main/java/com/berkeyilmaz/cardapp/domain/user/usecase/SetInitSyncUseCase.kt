package com.berkeyilmaz.cardapp.domain.user.usecase

import com.berkeyilmaz.cardapp.domain.user.UserRepository
import javax.inject.Inject

class SetInitSyncUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(value: Boolean) = repository.setInitSync(value)
}
