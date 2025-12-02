package com.berkeyilmaz.cardapp.domain.scan_result.model

import androidx.compose.ui.graphics.Color

enum class TagCategory(val displayName: String, val color: Color) {
    WORK("Work", Color(0xFF1976D2)),           // Blue
    SCHOOL("School", Color(0xFF388E3C)),       // Green
    HEALTH("Health", Color(0xFFD32F2F)),       // Red
    SERVICES("Services", Color(0xFFF57C00)),   // Orange
    EVENTS("Events", Color(0xFF7B1FA2)),       // Purple
    PERSONAL("Personal", Color(0xFF0097A7));   // Cyan

    companion object {
        fun fromString(value: String?): TagCategory {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            } ?: PERSONAL
        }
    }
}

