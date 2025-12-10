package com.berkeyilmaz.cardapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contactId: String,
    val userId: String,
    val filePath: String,
    val createdAt: Long = System.currentTimeMillis()
)
