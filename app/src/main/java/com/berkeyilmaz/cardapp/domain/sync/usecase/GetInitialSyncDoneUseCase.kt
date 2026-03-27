package com.berkeyilmaz.cardapp.domain.sync.usecase

import com.berkeyilmaz.cardapp.domain.sync.SyncRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInitialSyncDoneUseCase @Inject constructor(
    private val repository: SyncRepository
) {
    operator fun invoke(userId: String): Flow<Boolean> = repository.isInitialSyncDone(userId)
}
