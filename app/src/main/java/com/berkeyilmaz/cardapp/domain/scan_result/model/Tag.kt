package com.berkeyilmaz.cardapp.domain.scan_result.model

data class Tag(
    val category: String,
    val name: String,
)

fun Tag.getCategoryEnum(): TagCategory = TagCategory.fromString(category)
