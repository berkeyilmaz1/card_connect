package com.berkeyilmaz.cardapp.domain.scan_result.model

data class Tag(
    val category: String,
    val name: String,
) {
    fun getCategoryEnum(): TagCategory = TagCategory.fromString(category)
}
