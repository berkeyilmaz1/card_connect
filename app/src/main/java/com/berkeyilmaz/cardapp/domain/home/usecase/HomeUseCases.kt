package com.berkeyilmaz.cardapp.domain.home.usecase

import com.berkeyilmaz.cardapp.domain.home.HomeRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke() = repository.getCurrentUser()
}