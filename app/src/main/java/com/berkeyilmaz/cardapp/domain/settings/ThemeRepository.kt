package com.berkeyilmaz.cardapp.domain.settings

import com.berkeyilmaz.cardapp.domain.settings.model.AppTheme
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val themeFlow: Flow<AppTheme>
    suspend fun setTheme(theme: AppTheme)
}