package com.berkeyilmaz.cardapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.berkeyilmaz.cardapp.domain.settings.ThemeRepository
import com.berkeyilmaz.cardapp.domain.settings.model.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ThemeRepository {

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("app_theme")
    }

    override val themeFlow: Flow<AppTheme> = dataStore.data.map { preferences ->
        val themeName = preferences[PreferencesKeys.THEME] ?: AppTheme.SYSTEM.name
        try {
            AppTheme.valueOf(themeName)
        } catch (e: IllegalArgumentException) {
            AppTheme.SYSTEM
        }
    }

    override suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }
}