package com.berkeyilmaz.cardapp.presentation.settings.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.domain.settings.model.AppTheme
import com.berkeyilmaz.cardapp.domain.settings.model.Language
import com.berkeyilmaz.cardapp.domain.settings.usecase.GetLanguageUseCase
import com.berkeyilmaz.cardapp.domain.settings.usecase.GetThemeUseCase
import com.berkeyilmaz.cardapp.domain.settings.usecase.SaveLanguageUseCase
import com.berkeyilmaz.cardapp.domain.settings.usecase.SetThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val getLanguageUseCase: GetLanguageUseCase,
    private val saveLanguageUseCase: SaveLanguageUseCase
) : ViewModel() {

    // Language State
    private val _currentLanguage = MutableStateFlow(Language.ENGLISH)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    // Theme State
    val currentTheme: StateFlow<AppTheme> = getThemeUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppTheme.SYSTEM
    )

    init {
        observeLanguage()
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            getLanguageUseCase().collect { language ->
                Log.d("BerkeTAG", "Language changed to: ${language.code}")
                _currentLanguage.value = language
            }
        }
    }

    fun setLanguage(languageCode: String) {
        Log.d("BerkeTAG", "setLanguage called with: $languageCode")
        val language = Language.fromCode(languageCode)
        Log.d("BerkeTAG", "Language object created: $language")
        viewModelScope.launch {
            saveLanguageUseCase(language)
            Log.d("BerkeTAG", "Language saved: ${language.code}")
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            setThemeUseCase(theme)
        }
    }
}