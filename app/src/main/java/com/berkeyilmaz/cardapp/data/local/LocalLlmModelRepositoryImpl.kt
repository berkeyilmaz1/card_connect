package com.berkeyilmaz.cardapp.data.local

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.berkeyilmaz.cardapp.BuildConfig
import com.berkeyilmaz.cardapp.domain.settings.LocalLlmModelRepository
import com.berkeyilmaz.cardapp.domain.settings.LocalLlmModelState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalLlmModelRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : LocalLlmModelRepository {

    companion object {
        private const val TAG = "BerkeTag"

        // Gemma 3 1B model - Hugging Face'den indirilecek
        private const val MODEL_URL = "https://huggingface.co/litert-community/Gemma3-1B-IT/resolve/main/gemma3-1b-it-int4.task"
        private const val MODEL_FILENAME = "gemma3-1b-it-int4.task"
        private const val MODEL_DIR = "llm_models"
    }

    private val _modelState = MutableStateFlow<LocalLlmModelState>(LocalLlmModelState.NotDownloaded)
    override val modelState: StateFlow<LocalLlmModelState> = _modelState.asStateFlow()

    private var currentDownloadId: Long = -1
    private val downloadManager: DownloadManager by lazy {
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    init {
        // Uygulama başladığında model durumunu kontrol et
        checkModelStatus()
    }

    private fun checkModelStatus() {
        val modelFile = getModelFile()
        _modelState.value = if (modelFile.exists() && modelFile.length() > 0) {
            Log.i(TAG, "Model already downloaded: ${modelFile.absolutePath}")
            LocalLlmModelState.Ready
        } else {
            Log.i(TAG, "Model not found")
            LocalLlmModelState.NotDownloaded
        }
    }

    override suspend fun isModelDownloaded(): Boolean {
        return getModelFile().exists() && getModelFile().length() > 0
    }

    override fun resetErrorState() {
        if (_modelState.value is LocalLlmModelState.Error) {
            _modelState.value = LocalLlmModelState.NotDownloaded
            Log.i(TAG, "Error state reset to NotDownloaded")
        }
    }

    override suspend fun downloadModel(): Flow<LocalLlmModelState> = flow {
        emit(LocalLlmModelState.Downloading(0f))
        _modelState.value = LocalLlmModelState.Downloading(0f)

        try {
            // External files directory kullan (DownloadManager için gerekli)
            val externalDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val modelDir = File(externalDir, MODEL_DIR)
            if (!modelDir.exists()) {
                modelDir.mkdirs()
            }

            val destinationFile = File(modelDir, MODEL_FILENAME)

            // Eğer dosya zaten varsa sil
            if (destinationFile.exists()) {
                destinationFile.delete()
            }

            val request = DownloadManager.Request(MODEL_URL.toUri())
                .setTitle("LLM Model İndiriliyor")
                .setDescription("Gemma 3 1B model indiriliyor...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "$MODEL_DIR/$MODEL_FILENAME")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(false)
                .addRequestHeader("Authorization", "Bearer ${BuildConfig.HF_TOKEN}")

            currentDownloadId = downloadManager.enqueue(request)
            Log.i(TAG, "Download started with ID: $currentDownloadId")

            // İndirme durumunu takip et
            var isDownloading = true
            while (isDownloading) {
                val query = DownloadManager.Query().setFilterById(currentDownloadId)
                val cursor: Cursor? = downloadManager.query(query)

                cursor?.use {
                    if (it.moveToFirst()) {
                        val statusIndex = it.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val bytesDownloadedIndex = it.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                        val bytesTotalIndex = it.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)

                        val status = it.getInt(statusIndex)
                        val bytesDownloaded = it.getLong(bytesDownloadedIndex)
                        val bytesTotal = it.getLong(bytesTotalIndex)

                        when (status) {
                            DownloadManager.STATUS_RUNNING -> {
                                val progress = if (bytesTotal > 0) {
                                    (bytesDownloaded.toFloat() / bytesTotal.toFloat())
                                } else 0f

                                Log.d(TAG, "Download progress: ${(progress * 100).toInt()}%")
                                emit(LocalLlmModelState.Downloading(progress))
                                _modelState.value = LocalLlmModelState.Downloading(progress)
                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                Log.i(TAG, "Download completed successfully")
                                emit(LocalLlmModelState.Ready)
                                _modelState.value = LocalLlmModelState.Ready
                                isDownloading = false
                            }
                            DownloadManager.STATUS_FAILED -> {
                                val reasonIndex = it.getColumnIndex(DownloadManager.COLUMN_REASON)
                                val reason = it.getInt(reasonIndex)
                                val errorMessage = getDownloadErrorMessage(reason)
                                Log.e(TAG, "Download failed with reason code: $reason - $errorMessage")
                                emit(LocalLlmModelState.Error("Hata kodu: $reason - $errorMessage"))
                                _modelState.value = LocalLlmModelState.Error("Hata kodu: $reason - $errorMessage")
                                isDownloading = false
                            }
                            DownloadManager.STATUS_PAUSED -> {
                                Log.w(TAG, "Download paused")
                            }
                            DownloadManager.STATUS_PENDING -> {
                                Log.d(TAG, "Download pending...")
                            }
                        }
                    }
                }

                if (isDownloading) {
                    delay(500) // Her 500ms'de bir kontrol et
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Download error", e)
            val errorState = LocalLlmModelState.Error(e.message ?: "Bilinmeyen hata")
            emit(errorState)
            _modelState.value = errorState
        }
    }

    override fun cancelDownload() {
        if (currentDownloadId != -1L) {
            downloadManager.remove(currentDownloadId)
            currentDownloadId = -1
            _modelState.value = LocalLlmModelState.NotDownloaded
            Log.i(TAG, "Download cancelled")
        }
    }

    override suspend fun deleteModel() {
        withContext(Dispatchers.IO) {
            val modelFile = getModelFile()
            if (modelFile.exists()) {
                modelFile.delete()
                Log.i(TAG, "Model deleted")
            }
            _modelState.value = LocalLlmModelState.NotDownloaded
        }
    }

    override fun getModelPath(): String? {
        val modelFile = getModelFile()
        return if (modelFile.exists()) modelFile.absolutePath else null
    }

    private fun getModelFile(): File {
        val externalDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val modelDir = File(externalDir, MODEL_DIR)
        return File(modelDir, MODEL_FILENAME)
    }

    private fun getDownloadErrorMessage(reason: Int): String {
        return when (reason) {
            DownloadManager.ERROR_CANNOT_RESUME -> "İndirme devam ettirilemedi"
            DownloadManager.ERROR_DEVICE_NOT_FOUND -> "Depolama alanı bulunamadı"
            DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "Dosya zaten mevcut"
            DownloadManager.ERROR_FILE_ERROR -> "Dosya hatası"
            DownloadManager.ERROR_HTTP_DATA_ERROR -> "HTTP veri hatası"
            DownloadManager.ERROR_INSUFFICIENT_SPACE -> "Yetersiz depolama alanı"
            DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "Çok fazla yönlendirme"
            DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "HTTP hata kodu"
            DownloadManager.ERROR_UNKNOWN -> "Bilinmeyen hata"
            // HTTP status codes (400+)
            in 400..499 -> "HTTP Client Hatası: $reason (401=Unauthorized, 403=Forbidden, 404=Not Found)"
            in 500..599 -> "HTTP Server Hatası: $reason"
            else -> "Bilinmeyen hata kodu: $reason"
        }
    }
}

