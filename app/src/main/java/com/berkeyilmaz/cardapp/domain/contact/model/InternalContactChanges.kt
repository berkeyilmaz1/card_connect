package com.berkeyilmaz.cardapp.domain.contact.model

data class InternalContactChanges(
    val added: List<InternalContact> = emptyList(),
    val removed: List<InternalContact> = emptyList(),
    val modified: List<InternalContact> = emptyList()
) {
    val hasChanges: Boolean
        get() = added.isNotEmpty() || removed.isNotEmpty() || modified.isNotEmpty()
}

