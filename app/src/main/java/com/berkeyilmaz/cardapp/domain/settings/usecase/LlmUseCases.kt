package com.berkeyilmaz.cardapp.domain.settings.usecase

import com.berkeyilmaz.cardapp.domain.settings.LlmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUseLocalLlmUseCase @Inject constructor(
    private val repository: LlmRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.useLocalLlmFlow
}

class SetUseLocalLlmUseCase @Inject constructor(
    private val repository: LlmRepository
) {
    suspend operator fun invoke(useLocal: Boolean) {
        repository.setUseLocalLlm(useLocal)
    }
}

