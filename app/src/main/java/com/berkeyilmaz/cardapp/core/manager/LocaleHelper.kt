package com.berkeyilmaz.cardapp.core.manager

import android.content.Context
import android.content.res.Configuration
import com.berkeyilmaz.cardapp.domain.settings.model.Language
import java.util.Locale

object LocaleHelper {

    fun setLocale(context: Context, language: Language): Context {
        val locale = Locale.forLanguageTag(language.code)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    fun getLocalizedContext(context: Context, languageCode: String): Context {
        val language = Language.fromCode(languageCode)
        return setLocale(context, language)
    }
}