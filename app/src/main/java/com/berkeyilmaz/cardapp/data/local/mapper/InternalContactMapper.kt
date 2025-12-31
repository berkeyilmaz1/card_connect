package com.berkeyilmaz.cardapp.data.local.mapper

import com.berkeyilmaz.cardapp.data.local.entity.InternalContactEntity
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact

fun InternalContact.toEntity(): InternalContactEntity {
    return InternalContactEntity(
        contactId = contactId,
        fullName = fullName,
        phoneNumbers = phoneNumbers,
        emails = emails ?: emptyList(),
        websites = websites ?: emptyList(),
        organization = organization,
        title = title
    )
}

fun InternalContactEntity.toDomain(): InternalContact {
    return InternalContact(
        contactId = contactId,
        fullName = fullName,
        phoneNumbers = phoneNumbers,
        emails = emails.ifEmpty { null },
        websites = websites.ifEmpty { null },
        organization = organization,
        title = title
    )
}

fun List<InternalContact>.toEntityList(): List<InternalContactEntity> = map { it.toEntity() }

fun List<InternalContactEntity>.toDomainList(): List<InternalContact> = map { it.toDomain() }

