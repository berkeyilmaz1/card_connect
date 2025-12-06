package com.berkeyilmaz.cardapp.core.validation

interface BaseValidationRule {
    val errorMessageRes: Int
    fun validate(text: String): Boolean
}