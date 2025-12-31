package com.berkeyilmaz.cardapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.berkeyilmaz.cardapp.data.local.converter.StringListConverter

@Entity(tableName = "internal_contacts")
@TypeConverters(StringListConverter::class)
data class InternalContactEntity(
    @PrimaryKey
    val contactId: String,
    val fullName: String,
    val phoneNumbers: List<String>,
    val emails: List<String>,
    val websites: List<String>,
    val organization: String?,
    val title: String?,
)

