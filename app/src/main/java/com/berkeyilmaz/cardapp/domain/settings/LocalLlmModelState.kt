package com.berkeyilmaz.cardapp.domain.settings

/**
 * Local LLM Model durumlarını temsil eder
 */
sealed class LocalLlmModelState {
    /** Model henüz indirilmedi */
    data object NotDownloaded : LocalLlmModelState()

    /** Model indiriliyor */
    data class Downloading(val progress: Float) : LocalLlmModelState()

    /** Model indirildi ve kullanıma hazır */
    data object Ready : LocalLlmModelState()

    /** İndirme hatası */
    data class Error(val message: String) : LocalLlmModelState()
}

