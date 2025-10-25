package com.berkeyilmaz.cardapp.domain.theme.usecase

import com.berkeyilmaz.cardapp.domain.theme.ThemeRepository
import com.berkeyilmaz.cardapp.domain.theme.model.AppTheme
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