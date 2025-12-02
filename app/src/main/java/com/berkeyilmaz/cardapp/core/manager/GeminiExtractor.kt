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
You are an expert business card information extraction and classification agent.
Your task is to extract information from the OCR text and return structured JSON.

Input Text:
$recognizedText

STRICT OUTPUT FORMAT (JSON ONLY):
{
  "extractedData": {
    "fullName": "Name Surname",
    "title": "Job Title or Profession",
    "organization": "Company or School Name",
    "phones": ["+90..."],
    "emails": ["example@domain.com"],
    "websites": ["www.example.com"],
    "addresses": ["Full Address"],
    "socialMedia": ["@username", "linkedin.com/in/..."],

    "tags": [
      {
        "name": "TagName",
        "category": "WORK | SCHOOL | HEALTH | SERVICES | EVENTS | PERSONAL"
      }
    ]
  },
  "rawText": "$recognizedText"
}

--- EXTRACTION & TAGGING RULES ---

1. GENERAL EXTRACTION:
   - Detect phone numbers in any format.
   - Detect emails, websites, and addresses.
   - Extract 'organization' (company name, university, hospital, etc.)

2. TAGGING FORMAT:
   - Analyze the text, title, and email domain to decide the Main Category. 
   - MAIN CATEGORIES allowed: "WORK", "SCHOOL", "HEALTH", "SERVICES", "EVENTS", "PERSONAL".
   - Use UPPERCASE for category values.
   {
     "name": "...",
     "category": "..."
   }

3. TAG CATEGORIES DEFINITIONS (use UPPERCASE for category field):

   A) WORK
      - Corporate roles: Manager, CEO, Engineer, Developer, Director.
      - Corporate emails (not Gmail/Hotmail).
      - Tag name: Organization or Department.
      - Category: "WORK"

   B) SCHOOL
      - University, Student, Professor, ".edu" emails.
      - Tag name: School Name or Department.
      - Category: "SCHOOL"

   C) HEALTH
      - Titles like Dr., Dt., Uzm., Prof. Dr.
      - Mentions of Hospital, Clinic, Polyclinic.
      - Tag name: Medical Branch or Institution.
      - Category: "HEALTH"

   D) SERVICES
      - Service-based professions: Lawyer, Realtor, Barber, Plumber, Repair services.
      - Tag name: The Profession.
      - Category: "SERVICES"

   E) EVENTS
   - This category represents the event or place where the contact was met.
   - If the OCR contains names of expos, festivals, competitions, fairs, meetups, conferences, or summits, include this tag.
   - Tag name: The event name (e.g., "Teknofest 2025").
   - Category: "EVENTS"

   F) PERSONAL
      - Personal-use contacts: family, friends, individual phone numbers without job info.
      - Tag name: Relation.
      - Category: "PERSONAL"

5. CRITICAL RULES:
   - Only include tags if they make sense.
   - No duplicates.
   - NO MARKDOWN, ONLY CLEAN JSON.
""".trimIndent()


            // Gemini çağrısı
            val resultRaw = model.generateContent(prompt).text
            Log.d("BerkeTAG", "Gemini Raw Response: $resultRaw")

            val cleanedJson = cleanToJson(resultRaw ?: "")
            Log.d("BerkeTAG", "Cleaned JSON: $cleanedJson")

            // JSON → ScanResponse
            val scanResponse = Gson().fromJson(cleanedJson, ScanResponse::class.java)
            Log.d("BerkeTAG", "Parsed Tags: ${scanResponse.extractedData?.tags}")
            scanResponse
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
