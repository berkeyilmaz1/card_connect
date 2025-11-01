package com.berkeyilmaz.cardapp.domain.settings.usecase

import com.berkeyilmaz.cardapp.domain.settings.ThemeRepository
import com.berkeyilmaz.cardapp.domain.settings.model.AppTheme
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val repository: ThemeRepository
) {
    operator fun invoke(): Flow<AppTheme> = repository.themeFlow
}

class SetThemeUseCase @Inject constructor(
    private val repository: ThemeRepository
) {
    suspend operator fun invoke(theme: AppTheme) {
        repository.setTheme(theme)
    }
}