package com.berkeyilmaz.cardapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.berkeyilmaz.cardapp.domain.settings.LlmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LlmRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : LlmRepository {

    private object PreferencesKeys {
        val USE_LOCAL_LLM = booleanPreferencesKey("use_local_llm")
    }

    override val useLocalLlmFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USE_LOCAL_LLM] ?: false
    }

    override suspend fun setUseLocalLlm(useLocal: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_LOCAL_LLM] = useLocal
        }
    }
}

