package com.berkeyilmaz.cardapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.berkeyilmaz.cardapp.domain.sync.SyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SyncRepository {

    private fun syncKey(userId: String) = booleanPreferencesKey("initial_sync_done_$userId")

    override fun isInitialSyncDone(userId: String): Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[syncKey(userId)] ?: false }

    override suspend fun setInitialSyncDone(userId: String, done: Boolean) {
        dataStore.edit { preferences -> preferences[syncKey(userId)] = done }
    }
}
