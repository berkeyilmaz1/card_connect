package com.berkeyilmaz.cardapp.core.manager


import android.util.Log
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
You are a deterministic business card information extraction and classification agent.
You operate in a production environment where your output will be parsed automatically.

Your task:
* Extract structured contact information from OCR text
* Classify the contact using predefined categories
* Output ONLY valid JSON

--------------------
INPUT TEXT:
$recognizedText
--------------------

PROCESSING STEPS (DO NOT OUTPUT THESE STEPS):
1. Analyze the OCR text and identify entities (person, organization, contact details).
2. Normalize extracted values (phone, email, website).
3. Resolve ambiguities by choosing the most relevant and professional data.
4. Assign tags only when they clearly make sense.
5. Validate the final JSON structure before returning.
6. If any data is missing, use null or empty arrays as specified.

--------------------
STRICT OUTPUT FORMAT (JSON ONLY):
{
  "fullName": "Name Surname or Empty string",
  "title": "Job Title or Profession or Empty string",
  "organization": "Company / School / Institution Name or Empty string",
  "phones": [Phone numbers in international format or empty array],
  "emails": [Email addresses or empty array],
  "websites": [Website URLs or empty array],
  "address": "Full address as single string or Empty String",
  "socialMedias": [
    {
      "platform": "Platform Name or Empty String"",
      "url": "https://socialmedia.com/username or Empty String""
    }
  ],
  "tags": [
    {
      "name": "TagName",
      "category": "WORK | SCHOOL | HEALTH | SERVICES | EVENTS | PERSONAL"
    }
  ],
  "note": Empty string,
}

--------------------
EXTRACTION RULES:

GENERAL:
* Detect phone numbers in any international or local format.
* Normalize phone numbers to international format if possible.
* Detect emails, websites, addresses, and social links.
* Extract organization names from company, school, hospital, or institution mentions.

MISSING DATA:
* If a field is not found, use null (for strings) or [] (for arrays).
* Never invent data.

--------------------
TAGGING RULES:

MAIN CATEGORIES (UPPERCASE ONLY):
WORK | SCHOOL | HEALTH | SERVICES 

CATEGORY DEFINITIONS:

WORK:
* Corporate or professional roles (Engineer, Manager, Developer, CEO).
* Tag name: Organization or department.

SCHOOL:
* Universities, students, professors, ".edu" domains.
* Tag name: School name or faculty.

HEALTH:
* Medical titles (Dr., Dt., Prof. Dr., Uzm.).
* Hospitals, clinics, polyclinics.
* Tag name: Medical institution or branch.

SERVICES:
* Service providers (Lawyer, Realtor, Barber, Technician).
* Tag name: The profession.
--------------------
CRITICAL CONSTRAINTS:
* NO markdown
* NO explanations
* NO extra fields
* NO duplicate tags
* Output MUST be valid JSON
""".trimIndent()

            val resultRaw = model.generateContent(prompt).text
            Log.d("BerkeTAG", "Gemini Raw Response: $resultRaw")

            val cleanedJson = cleanToJson(resultRaw ?: "")
            Log.d("BerkeTAG", "Cleaned JSON: $cleanedJson")

            // JSON → ScanResponse
            val scanResponse = Gson().fromJson(cleanedJson, ScanResponse::class.java)
            Log.d("BerkeTAG", "Parsed Tags: ${scanResponse.tags}")
            scanResponse
        }

    suspend fun findContactThatUserAsked(
        userQuestion: String, contacts: List<Contact>
    ): List<Contact> = withContext(Dispatchers.IO) {

        val contactsJson = Gson().toJson(contacts)

        val prompt = """
You are an AI contact resolution engine inside a mobile contacts application.

Your goal:
- Determine which contact(s) the user is referring to.
- Use semantic meaning, job titles, organizations, and tags.
- Prefer returning EXACTLY ONE contact whenever confidence is high.
- Only return MULTIPLE contacts if more than one contact is equally valid.

IMPORTANT:
- Think internally, but DO NOT expose your reasoning.
- Return ONLY valid JSON.
- NO markdown, NO comments, NO explanations.

====================
OUTPUT FORMAT RULES
====================

If ONE contact matches:
{
  "contactId": "string",
  "fullName": "string",
  "title": "string",
  "organization": "string",
  "phones": ["string"],
  "emails": ["string"],
  "reason": "short explanation"
}

If MULTIPLE contacts match:
[
  {
    "contactId": "string",
    "fullName": "string",
    "title": "string",
    "organization": "string",
    "phones": ["string"],
    "emails": ["string"],
    "reason": "short explanation"
  }
  ...
]

If NO contact matches:
[]

====================
USER QUERY
====================
"$userQuestion"

====================
CONTACTS (JSON)
====================
$contactsJson
""".trimIndent()

        Log.d("BerkeTAG", "Gemini Prompt: $prompt")
        val raw = model.generateContent(prompt).text.orEmpty()
        Log.d("BerkeTAG", "Gemini Raw Response: $raw")

        val cleaned = cleanToJson(raw)
        Log.d("BerkeTAG", "Cleaned JSON: $cleaned")

        return@withContext runCatching {

            // JSON object --> single result
            if (cleaned.trim().startsWith("{")) {
                listOf(Gson().fromJson(cleaned, Contact::class.java))
            }
            // JSON array --> multiple result
            else if (cleaned.trim().startsWith("[")) {
                Gson().fromJson(cleaned, Array<Contact>::class.java).toList()
            } else emptyList()

        }.getOrElse {
            Log.e("BerkeTAG", "Parse error: ${it.message}")
            emptyList()
        }
    }

    /** Yeni eklenen rehber kullanıcılarına tag öner*/
    suspend fun suggestTagsForNewContact(contacts: List<InternalContact>): List<ContactRequest> =
        withContext(Dispatchers.IO) {

            val contactsJson = Gson().toJson(contacts)

            val prompt = """
You are a deterministic business card information extraction and classification agent.
You operate in a production environment where your output will be parsed automatically.

Your task:
* Extract structured contact information from the given contact list
* Classify each contact using predefined categories
* Output ONLY valid JSON array

--------------------
INPUT CONTACTS (JSON ARRAY):
$contactsJson
--------------------

PROCESSING STEPS (DO NOT OUTPUT THESE STEPS):
1. Analyze each contact and identify entities (person, organization, contact details).
2. Normalize extracted values (phone, email, website).
3. Resolve ambiguities by choosing the most relevant and professional data.
4. Assign tags only when they clearly make sense.
5. Validate the final JSON structure before returning.
6. If any data is missing, use null or empty arrays as specified.
7. Return a JSON array with one ContactRequest object for each input contact.

--------------------
STRICT OUTPUT FORMAT (JSON ARRAY ONLY):
[
  {
    "fullName": "Name Surname or Empty String"",
    "title": "Job Title or Profession or Empty String"",
    "organization": "Company / School / Institution Name or Empty String"",
    "phones": [Phone numbers in international format or empty array],
    "emails": [Email addresses or empty array],
    "websites": [Website URLs or empty array],
    "address": "Full address as single string or Empty String"",
    "socialMedias": [
      {
        "platform": "Platform Name",
        "url": "https://socialmedia.com/username or Empty String""
      }
    ],
    "tags": [
      {
        "name": "TagName",
        "category": "WORK | SCHOOL | HEALTH | SERVICES | EVENTS | PERSONAL"
      }
    ],
    "from": "PHONEBOOK"
  }
]

--------------------
EXTRACTION RULES:

GENERAL:
* Detect phone numbers in any international or local format.
* Normalize phone numbers to international format if possible.
* Detect emails, websites, addresses, and social links.
* Extract organization names from company, school, hospital, or institution mentions.

MISSING DATA:
* If a field is not found, use null (for strings) or [] (for arrays).
* Never invent data.

--------------------
TAGGING RULES:

MAIN CATEGORIES (UPPERCASE ONLY):
WORK | SCHOOL | HEALTH | SERVICES 

CATEGORY DEFINITIONS:

WORK:
* Corporate or professional roles (Engineer, Manager, Developer, CEO).
* Tag name: Organization or department.

SCHOOL:
* Universities, students, professors, ".edu" domains.
* Tag name: School name or faculty.

HEALTH:
* Medical titles (Dr., Dt., Prof. Dr., Uzm.).
* Hospitals, clinics, polyclinics.
* Tag name: Medical institution or branch.

SERVICES:
* Service providers (Lawyer, Realtor, Barber, Technician).
* Tag name: The profession.
--------------------
CRITICAL CONSTRAINTS:
* NO markdown
* NO explanations
* NO extra fields
* NO duplicate tags
* Output MUST be valid JSON
""".trimIndent()
            Log.d("BerkeTAG", "Gemini Prompt: $prompt")
            val resultRaw = model.generateContent(prompt).text
            Log.d("BerkeTAG", "Gemini Raw Response: $resultRaw")

            val cleanedJson = cleanToJson(resultRaw ?: "")
            Log.d("BerkeTAG", "Cleaned JSON: $cleanedJson")

            // JSON → List<ContactRequest>
            val contactRequests: List<ContactRequest> = try {
                val type = object : TypeToken<List<ContactRequest>>() {}.type
                Gson().fromJson(cleanedJson, type)
            } catch (e: Exception) {
                Log.e("BerkeTAG", "Failed to parse as array, trying single object: ${e.message}")
                val single = Gson().fromJson(cleanedJson, ContactRequest::class.java)
                listOf(single)
            }
            Log.d("BerkeTAG", "Parsed ${contactRequests.size} ContactRequests")
            contactRequests
        }

    /**
     * Gemini bazen JSON dışı metin ekleyebilir, bu fonksiyon bunu temizler.
     */
    private fun cleanToJson(input: String): String {
        val firstObj = input.indexOf('{')
        val firstArr = input.indexOf('[')

        val start = listOf(firstObj, firstArr).filter { it >= 0 }.minOrNull() ?: return input

        val end = if (start == firstObj) input.lastIndexOf('}')
        else input.lastIndexOf(']')

        if (end <= start) return input
        return input.substring(start, end + 1)
    }

}
