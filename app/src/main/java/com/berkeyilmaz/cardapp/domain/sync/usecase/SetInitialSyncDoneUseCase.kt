package com.berkeyilmaz.cardapp.domain.sync.usecase

import com.berkeyilmaz.cardapp.domain.sync.SyncRepository
import javax.inject.Inject

class SetInitialSyncDoneUseCase @Inject constructor(
    private val repository: SyncRepository
) {
    suspend operator fun invoke(userId: String, done: Boolean) = repository.setInitialSyncDone(userId, done)
}
