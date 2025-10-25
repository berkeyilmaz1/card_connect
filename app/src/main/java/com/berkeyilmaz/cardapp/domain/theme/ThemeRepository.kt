package com.berkeyilmaz.cardapp.domain.theme


import com.berkeyilmaz.cardapp.domain.theme.model.AppTheme
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val themeFlow: Flow<AppTheme>
    suspend fun setTheme(theme: AppTheme)
}