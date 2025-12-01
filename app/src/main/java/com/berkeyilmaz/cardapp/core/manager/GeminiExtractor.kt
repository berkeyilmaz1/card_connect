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
    Your task is to extract information from the OCR text and categorize the contact into hierarchical groups.

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
        
        "groups": {
          "Work": ["SubTag1", "SubTag2"],
          "School": ["SubTag1"],
          "Health": ["SubTag1"],
          "Services": ["SubTag1"],
          "Events": ["SubTag1"],
          "Personal": ["SubTag1"]
        },
      },
      "rawText": "$recognizedText"
    }

    --- EXTRACTION & CLASSIFICATION RULES ---
    
    1. GENERAL EXTRACTION:
       - Detect phone numbers in any format.
       - Detect emails, websites, and addresses.
       - Extract 'organization' (Company name, University name, or Hospital name).
    
    2. HIERARCHICAL GROUPING LOGIC (Populate 'groups' object):
       - Analyze the text, title, and email domain to decide the Main Category.
       - MAIN CATEGORIES allowed: "Work", "School", "Health", "Services", "Events", "Personal".
       
       - LOGIC FOR 'Work': 
         If the text contains corporate titles (Manager, CEO, Engineer) or a corporate email (not gmail/hotmail), add "Work".
         > Sub-tag: The Organization Name (e.g., "Trendyol", "Google", "Hepsiburada","Hubx","Yemeksepeti","Facebook","Uber").
         
       - LOGIC FOR 'School':
         If the text contains "University", "Student", "Professor", or ".edu" email, add "School".
         > Sub-tag: The School Name (e.g., "Düzce University").
         
       - LOGIC FOR 'Health':
         If the title is "Dr.", "Dt.", or text contains "Hospital", "Clinic", add "Health".
         > Sub-tag: The Branch or Hospital Name (e.g., "Dentist", "Acibadem Hospital").
         
       - LOGIC FOR 'Services':
         If the profession is service-based (Lawyer, Realtor, Barber, Plumber), add "Services".
         > Sub-tag: The Profession (e.g., "Lawyer", "Real Estate").

    3. CRITICAL RULES:
       - Only include categories in 'groups' if relevant data is found.
       - Remove duplicates.
       - If a specific sub-tag cannot be found (e.g. company name is missing), use the Title as sub-tag.
       - Do NOT include any markdown formatting (like ```json). Just the raw JSON string.
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
