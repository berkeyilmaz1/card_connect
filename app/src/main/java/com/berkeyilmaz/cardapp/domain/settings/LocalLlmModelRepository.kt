package com.berkeyilmaz.cardapp.domain.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Local LLM Model yönetimi için repository interface
 */
interface LocalLlmModelRepository {
    /** Model durumu */
    val modelState: StateFlow<LocalLlmModelState>

    /** Model dosyası mevcut mu? */
    suspend fun isModelDownloaded(): Boolean

    /** Modeli indir */
    suspend fun downloadModel(): Flow<LocalLlmModelState>

    /** İndirmeyi iptal et */
    fun cancelDownload()

    /** Model dosyasını sil */
    suspend fun deleteModel()

    /** Model dosya yolu */
    fun getModelPath(): String?

    /** Error state'i reset et */
    fun resetErrorState()
}

