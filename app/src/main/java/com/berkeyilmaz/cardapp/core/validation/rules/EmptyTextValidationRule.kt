package com.berkeyilmaz.cardapp.core.validation.rules

import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.validation.BaseValidationRule

class EmptyTextValidationRule(override val errorMessageRes: Int = R.string.please_fill_the_field) :
    BaseValidationRule {
    override fun validate(text: String): Boolean {
        return text.isNotEmpty()
    }
}