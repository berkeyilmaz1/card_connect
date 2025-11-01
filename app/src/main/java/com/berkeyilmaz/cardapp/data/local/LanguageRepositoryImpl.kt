package com.berkeyilmaz.cardapp.data.local

import com.berkeyilmaz.cardapp.core.cache.LanguagePreferences
import com.berkeyilmaz.cardapp.domain.LanguageRepository
import com.berkeyilmaz.cardapp.domain.settings.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LanguageRepositoryImpl @Inject constructor(
    private val languagePreferences: LanguagePreferences
) : LanguageRepository {

    override suspend fun saveLanguage(language: Language) {
        languagePreferences.saveLanguageCode(language.code)
    }

    override fun getLanguage(): Flow<Language> {
        return languagePreferences.languageCode.map { code ->
            Language.fromCode(code)
        }
    }
}