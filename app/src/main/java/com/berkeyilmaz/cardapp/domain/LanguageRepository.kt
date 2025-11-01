package com.berkeyilmaz.cardapp.domain

import com.berkeyilmaz.cardapp.domain.settings.model.Language
import kotlinx.coroutines.flow.Flow


interface LanguageRepository {
    suspend fun saveLanguage(language: Language)
    fun getLanguage(): Flow<Language>
}