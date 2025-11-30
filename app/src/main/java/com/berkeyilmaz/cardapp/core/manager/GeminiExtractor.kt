package com.berkeyilmaz.cardapp.core.manager


import android.util.Log
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiExtractor {

    private val model by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel("gemini-2.5-flash")
    }

    /**
     * Kartvizit metnini Gemini'a gönderir ve anlamlandırılmış ScanResponse döndürür.
     */
    suspend fun extractFromText(recognizedText: String): ScanResponse =
        withContext(Dispatchers.IO) {

            val prompt = """
                You are a business card information extraction model.
                Extract the following text into a strict JSON format that matches this schema:
                
                {
                  "extractedData": {
                    "fullName": "",
                    "title": "",
                    "organization": "",
                    "phones": [],
                    "emails": [],
                    "websites": [],
                    "addresses": [],
                    "socialMedia": [],
                    "tags": [],
                    "note": ""
                  },
                  "rawText": ""
                }
                
                Rules:
                - Detect phone numbers in any format.
                - Detect emails with or without separators.
                - Detect websites and links.
                - Detect addresses even if partial.
                - Detect full name from patterns like 'Name Surname'.
                - Detect job title if present (CEO, Manager, Developer, etc.).
                - Detect company/organization names.
                - Remove duplicates.
                - Do NOT include any explanation, only return valid JSON.
                
                Text to analyze: 
                $recognizedText
                
                IMPORTANT: Return ONLY JSON.
            """.trimIndent()

            // Gemini çağrısı
            val resultRaw = model.generateContent(prompt).text
            Log.d("BerkeTAG", resultRaw ?: "null result")

            val cleanedJson = cleanToJson(resultRaw ?: "")
            Log.d("BerkeTAG", "Cleaned JSON: $cleanedJson")

            // JSON → ScanResponse
            Gson().fromJson(cleanedJson, ScanResponse::class.java)
        }

    /**
     * Gemini bazen JSON dışı metin ekleyebilir, bu fonksiyon bunu temizler.
     */
    private fun cleanToJson(input: String): String {
        // İlk '{' ve son '}' arasını alır
        val start = input.indexOf('{')
        val end = input.lastIndexOf('}')

        if (start == -1 || end == -1 || end <= start) return input

        return input.substring(start, end + 1)
    }
}
