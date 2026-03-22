package com.berkeyilmaz.cardapp.presentation.settings.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.core.analytics.AnalyticsManager
import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.berkeyilmaz.cardapp.domain.auth.usecase.GetCurrentUserUseCase
import com.berkeyilmaz.cardapp.domain.settings.LocalLlmModelRepository
import com.berkeyilmaz.cardapp.domain.settings.LocalLlmModelState
import com.berkeyilmaz.cardapp.domain.settings.model.AppTheme
import com.berkeyilmaz.cardapp.domain.settings.model.Language
import com.berkeyilmaz.cardapp.domain.settings.usecase.GetLanguageUseCase
import com.berkeyilmaz.cardapp.domain.settings.usecase.GetThemeUseCase
import com.berkeyilmaz.cardapp.domain.settings.usecase.GetUseLocalLlmUseCase
import com.berkeyilmaz.cardapp.domain.settings.usecase.SaveLanguageUseCase
import com.berkeyilmaz.cardapp.domain.settings.usecase.SetThemeUseCase
import com.berkeyilmaz.cardapp.domain.settings.usecase.SetUseLocalLlmUseCase
import com.berkeyilmaz.cardapp.domain.user.UserRepository
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
    private val saveLanguageUseCase: SaveLanguageUseCase,
    private val getUseLocalLlmUseCase: GetUseLocalLlmUseCase,
    private val setUseLocalLlmUseCase: SetUseLocalLlmUseCase,
    private val localLlmModelRepository: LocalLlmModelRepository,
    private val userRepository: UserRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val analyticsManager: AnalyticsManager
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

    // Local LLM State
    val useLocalLlm: StateFlow<Boolean> = getUseLocalLlmUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // Local LLM Model State
    val localLlmModelState: StateFlow<LocalLlmModelState> = localLlmModelRepository.modelState

    // İndirme dialog durumu
    private val _showDownloadDialog = MutableStateFlow(false)
    val showDownloadDialog: StateFlow<Boolean> = _showDownloadDialog.asStateFlow()

    // Analytics Consent State
    private val _analyticsConsent = MutableStateFlow(false)
    val analyticsConsent: StateFlow<Boolean> = _analyticsConsent.asStateFlow()

    init {
        observeLanguage()
        loadAnalyticsConsent()
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

    fun setUseLocalLlm(useLocal: Boolean) {
        viewModelScope.launch {
            // Eğer local LLM aktif edilmek isteniyorsa ve model indirilmemişse
            if (useLocal && localLlmModelState.value !is LocalLlmModelState.Ready) {
                // Dialog göster, switch'i henüz aktif etme
                _showDownloadDialog.value = true
            } else {
                setUseLocalLlmUseCase(useLocal)
            }
        }
    }

    fun dismissDownloadDialog() {
        _showDownloadDialog.value = false
    }

    fun startModelDownload() {
        viewModelScope.launch {
            _showDownloadDialog.value = false
            localLlmModelRepository.downloadModel().collect { state ->
                Log.d("SettingsViewModel", "Download state: $state")
                // İndirme tamamlandığında local LLM'i aktif et
                if (state is LocalLlmModelState.Ready) {
                    setUseLocalLlmUseCase(true)
                }
            }
        }
    }

    fun cancelModelDownload() {
        localLlmModelRepository.cancelDownload()
    }

    fun deleteModel() {
        viewModelScope.launch {
            // Önce local LLM'i devre dışı bırak
            setUseLocalLlmUseCase(false)
            // Sonra modeli sil
            localLlmModelRepository.deleteModel()
        }
    }

    fun dismissErrorDialog() {
        localLlmModelRepository.resetErrorState()
    }

    private fun loadAnalyticsConsent() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            if (user is ResponseState.Success) {
                val uid = user.data?.uid ?: return@launch
                val result = userRepository.getAnalyticsConsent(uid)
                if (result is ResponseState.Success) {
                    _analyticsConsent.value = result.data
                    analyticsManager.setAnalyticsEnabled(result.data)
                }
            }
        }
    }

    fun setAnalyticsConsent(enabled: Boolean) {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            if (user is ResponseState.Success) {
                val uid = user.data?.uid ?: return@launch
                userRepository.saveAnalyticsConsent(uid, enabled)
                analyticsManager.setAnalyticsEnabled(enabled)
                _analyticsConsent.value = enabled
            }
        }
    }
}