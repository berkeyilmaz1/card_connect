package com.berkeyilmaz.cardapp.domain.sync

import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    fun isInitialSyncDone(userId: String): Flow<Boolean>
    suspend fun setInitialSyncDone(userId: String, done: Boolean)
}

