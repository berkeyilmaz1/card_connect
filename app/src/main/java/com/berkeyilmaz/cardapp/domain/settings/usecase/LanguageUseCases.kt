package com.berkeyilmaz.cardapp.domain.settings.usecase

import com.berkeyilmaz.cardapp.domain.LanguageRepository
import com.berkeyilmaz.cardapp.domain.settings.model.Language
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLanguageUseCase @Inject constructor(
    private val repository: LanguageRepository
) {
    operator fun invoke(): Flow<Language> = repository.getLanguage()
}

class SaveLanguageUseCase @Inject constructor(
    private val repository: LanguageRepository
) {
    suspend operator fun invoke(language: Language) {
        repository.saveLanguage(language)
    }
}

