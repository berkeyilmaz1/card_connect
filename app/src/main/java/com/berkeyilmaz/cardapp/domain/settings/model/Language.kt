package com.berkeyilmaz.cardapp.domain.settings.model

enum class Language(val code: String, val displayName: String) {
    TURKISH("tr", "Türkçe"),
    ENGLISH("en", "English");

    companion object {
        fun fromCode(code: String): Language {
            return entries.find { it.code == code } ?: ENGLISH
        }
    }
}