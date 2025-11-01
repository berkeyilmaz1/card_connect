package com.berkeyilmaz.cardapp

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.datastore.preferences.core.stringPreferencesKey
import com.berkeyilmaz.cardapp.core.cache.languageDataStore
import com.berkeyilmaz.cardapp.domain.settings.model.Language
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.Locale


@HiltAndroidApp
class MyApplication : Application() {

    override fun attachBaseContext(base: Context) {
        val localizedContext = runBlocking {
            try {
                val languageKey = stringPreferencesKey("language_code")
                val languageCode = base.languageDataStore.data.map { preferences ->
                    preferences[languageKey] ?: Language.ENGLISH.code
                }.first()

                val language = Language.fromCode(languageCode)
                applyLanguage(base, language)
            } catch (_: Exception) {
                base
            }
        }
        super.attachBaseContext(localizedContext)
    }

    private fun applyLanguage(context: Context, language: Language): Context {
        val locale = Locale.forLanguageTag(language.code)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}

