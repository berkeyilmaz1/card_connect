package com.berkeyilmaz.cardapp.domain.scan_result.model

data class Tag(
    val name: String,
    val category: String
) {
    fun getCategoryEnum(): TagCategory = TagCategory.fromString(category)
}
