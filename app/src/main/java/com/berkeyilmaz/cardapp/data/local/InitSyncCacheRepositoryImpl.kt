package com.berkeyilmaz.cardapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InitSyncCacheRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private fun syncKey(userId: String) = booleanPreferencesKey("init_sync_done_$userId")

    suspend fun isInitSyncDone(userId: String): Boolean =
        dataStore.data.first()[syncKey(userId)] ?: false

    suspend fun markInitSyncDone(userId: String) {
        dataStore.edit { it[syncKey(userId)] = true }
    }
}
