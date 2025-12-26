package com.berkeyilmaz.cardapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.berkeyilmaz.cardapp.data.local.dao.PhotoDAO
import com.berkeyilmaz.cardapp.data.local.entity.PhotoEntity

@Database(
    entities = [PhotoEntity::class], version = 1, exportSchema = false
)
abstract class CardConnectDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDAO
}