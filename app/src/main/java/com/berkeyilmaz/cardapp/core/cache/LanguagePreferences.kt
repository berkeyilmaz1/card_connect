package com.berkeyilmaz.cardapp.core.cache

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.berkeyilmaz.cardapp.domain.settings.model.Language
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(name = "language_preferences")

@Singleton
class LanguagePreferences @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
    }

    suspend fun saveLanguageCode(languageCode: String) {
        Log.d("BerkeTAG", "Saving language code: $languageCode")
        context.languageDataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE_CODE] = languageCode
        }
        Log.d("BerkeTAG", "Language code saved successfully")
    }

    val languageCode: Flow<String> = context.languageDataStore.data.map { preferences ->
        preferences[PreferencesKeys.LANGUAGE_CODE] ?: Language.ENGLISH.code
    }
}