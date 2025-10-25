package com.berkeyilmaz.cardapp.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.domain.theme.model.AppTheme
import com.berkeyilmaz.cardapp.domain.theme.usecase.GetThemeUseCase
import com.berkeyilmaz.cardapp.domain.theme.usecase.SetThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase
) : ViewModel() {
    val currentTheme: StateFlow<AppTheme> = getThemeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM
        )

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            setThemeUseCase(theme)
        }
    }
}