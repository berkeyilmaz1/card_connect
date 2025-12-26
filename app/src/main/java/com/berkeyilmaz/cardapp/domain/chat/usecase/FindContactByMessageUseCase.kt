package com.berkeyilmaz.cardapp.domain.chat.usecase

import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import javax.inject.Inject

class FindContactByMessageUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(text: String, contacts: List<Contact>): AIContactResult {
        val result = repository.searchContactThatUserAsked(text, contacts)
        return when {
            result.isEmpty() -> AIContactResult.NoResult
            result.size == 1 -> AIContactResult.Single(result.first())
            else -> AIContactResult.Multiple(result)
        }
    }
}

sealed class AIContactResult {
    data object NoResult : AIContactResult()
    data class Single(val contact: Contact) : AIContactResult()
    data class Multiple(val contacts: List<Contact>) : AIContactResult()
}