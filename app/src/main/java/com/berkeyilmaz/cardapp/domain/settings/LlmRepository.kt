package com.berkeyilmaz.cardapp.domain.settings

import kotlinx.coroutines.flow.Flow

interface LlmRepository {
    val useLocalLlmFlow: Flow<Boolean>
    suspend fun setUseLocalLlm(useLocal: Boolean)
}

