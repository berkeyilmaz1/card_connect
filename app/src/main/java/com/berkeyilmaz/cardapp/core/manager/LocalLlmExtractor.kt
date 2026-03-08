package com.berkeyilmaz.cardapp.core.manager

import android.content.Context
import android.util.Log
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import com.berkeyilmaz.cardapp.domain.settings.LocalLlmModelRepository
import com.berkeyilmaz.cardapp.domain.settings.LocalLlmModelState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MediaPipe LLM Inference API kullanarak local olarak çalışan LLM Extractor
 *
 * NOT: MediaPipe LLM Inference API henüz experimental olduğundan,
 * API değişikliklerine hazırlıklı olunmalıdır.
 */
@Singleton
class LocalLlmExtractor @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val localLlmModelRepository: LocalLlmModelRepository
) {
    companion object {
        private const val TAG = "BerkeTag"
        private const val MAX_TOKENS = 4096
        private const val TOP_K = 40
    }

    private var llmInference: LlmInference? = null

    /**
     * Model hazır mı kontrol et
     */
    fun isModelReady(): Boolean {
        return localLlmModelRepository.modelState.value is LocalLlmModelState.Ready
    }

    /**
     * LLM inference'ı başlat
     */
    private suspend fun initializeLlm(): Boolean = withContext(Dispatchers.IO) {
        if (llmInference != null) {
            return@withContext true
        }

        val modelPath = localLlmModelRepository.getModelPath()
        if (modelPath == null) {
            Log.e(TAG, "Model path is null - model not downloaded")
            return@withContext false
        }

        try {
            // MediaPipe LLM Inference API kullanarak model yükle
            Log.i(TAG, "Attempting to initialize LLM with model: $modelPath")
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTokens(MAX_TOKENS)
                .setMaxTopK(TOP_K)
                .build()
            llmInference = LlmInference.createFromOptions(context, options)

            Log.i(TAG, "LLM Inference initialized successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize LLM Inference", e)
            false
        }
    }

    /**
     * Kartvizit metnini analiz ederek ScanResponse döndürür
     */
    suspend fun extractFromText(recognizedText: String): ScanResponse? =
        withContext(Dispatchers.IO) {
            if (!isModelReady()) {
                Log.w(TAG, "Model not ready")
                return@withContext null
            }

            val initialized = initializeLlm()
            if (!initialized) {
                Log.e(TAG, "Failed to initialize LLM")
                return@withContext null
            }

            val prompt = buildExtractionPrompt(recognizedText)

            try {
                Log.d(TAG, "Sending prompt to Local LLM")

                val response = llmInference?.generateResponse(prompt)

                if (response == null) {
                    Log.w(TAG, "LLM returned null response")
                    return@withContext null
                }

                Log.d(TAG, "Local LLM Raw Response: $response")

                val cleanedJson = cleanToJson(response)
                Log.d(TAG, "Cleaned JSON: $cleanedJson")

                val scanResponse = Gson().fromJson(cleanedJson, ScanResponse::class.java)
                Log.d(TAG, "Parsed ScanResponse: $scanResponse")
                scanResponse
            } catch (e: Exception) {
                Log.e(TAG, "Error extracting from text", e)
                null
            }
        }

    /**
     * Kullanıcının sorduğu kişiyi bul
     */
    suspend fun findContactThatUserAsked(
        userQuestion: String,
        contacts: List<Contact>
    ): List<Contact> = withContext(Dispatchers.IO) {
        if (!isModelReady()) {
            Log.w(TAG, "Model not ready")
            return@withContext emptyList()
        }

        val initialized = initializeLlm()
        if (!initialized) {
            Log.e(TAG, "Failed to initialize LLM")
            return@withContext emptyList()
        }

        val contactsJson = Gson().toJson(contacts)
        val prompt = buildSearchPrompt(userQuestion, contactsJson)

        try {
            Log.d(TAG, "Sending search prompt to Local LLM")

            val response = llmInference?.generateResponse(prompt)

            if (response == null) {
                Log.w(TAG, "LLM returned null response")
                return@withContext emptyList()
            }

            Log.d(TAG, "Local LLM Search Response: $response")

            val cleaned = cleanToJson(response)
            parseContactSearchResponse(cleaned, contacts)
        } catch (e: Exception) {
            Log.e(TAG, "Error searching contacts", e)
            emptyList()
        }
    }

    /**
     * Yeni contactlar için tag önerisi
     */
    suspend fun suggestTagsForNewContact(
        contacts: List<InternalContact>
    ): List<ContactRequest> = withContext(Dispatchers.IO) {
        if (!isModelReady()) {
            Log.w(TAG, "Model not ready")
            return@withContext emptyList()
        }

        val initialized = initializeLlm()
        if (!initialized) {
            Log.e(TAG, "Failed to initialize LLM")
            return@withContext emptyList()
        }

        val contactsJson = Gson().toJson(contacts)
        val prompt = buildTagSuggestionPrompt(contactsJson)

        try {
            Log.d(TAG, "Sending tag suggestion prompt to Local LLM")

            val response = llmInference?.generateResponse(prompt)

            if (response == null) {
                Log.w(TAG, "LLM returned null response")
                return@withContext emptyList()
            }

            Log.d(TAG, "Local LLM Tag Response: $response")

            val cleaned = cleanToJson(response)
            val type = object : TypeToken<List<ContactRequest>>() {}.type
            Gson().fromJson(cleaned, type) ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error suggesting tags", e)
            emptyList()
        }
    }

    /**
     * LLM'i kapat ve kaynakları serbest bırak
     */
    fun close() {
        llmInference?.close()
        llmInference = null
        Log.i(TAG, "LLM Inference closed")
    }

    // ==================== PROMPT BUILDERS ====================

    private fun buildExtractionPrompt(recognizedText: String): String {
        return """
Extract contact info from this text. Return ONLY JSON:

TEXT: $recognizedText

JSON FORMAT:
{"fullName":"","title":"","organization":"","phones":[],"emails":[],"websites":[],"address":"","socialMedias":[],"tags":[],"note":""}
""".trimIndent()
    }

    private fun buildSearchPrompt(userQuestion: String, contactsJson: String): String {
        return """
Find matching contactIds for query "$userQuestion" from:
$contactsJson

Return ONLY JSON array of IDs: ["id1","id2"] or []
""".trimIndent()
    }

    private fun buildTagSuggestionPrompt(contactsJson: String): String {
        return """
Suggest tags for contacts. Return ONLY JSON array.

INPUT: $contactsJson

OUTPUT FORMAT:
[{"fullName":"","title":"","organization":"","phones":[],"emails":[],"websites":[],"address":"","tags":[{"name":"","category":"WORK|SCHOOL|HEALTH|SERVICES"}],"note":""}]
""".trimIndent()
    }

    // ==================== HELPERS ====================

    private fun cleanToJson(raw: String): String {
        var cleaned = raw.trim()

        // Remove markdown code blocks
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.removePrefix("```json")
        }
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.removePrefix("```")
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.removeSuffix("```")
        }

        return cleaned.trim()
    }

    private fun parseContactSearchResponse(json: String, contacts: List<Contact>): List<Contact> {
        return try {
            // JSON array of contact IDs
            val type = object : TypeToken<List<String>>() {}.type
            val ids: List<String> = Gson().fromJson(json, type) ?: emptyList()
            contacts.filter { it.contactId in ids }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing search response", e)
            emptyList()
        }
    }
}

