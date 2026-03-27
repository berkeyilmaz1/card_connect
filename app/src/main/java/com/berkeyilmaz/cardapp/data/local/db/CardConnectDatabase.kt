package com.berkeyilmaz.cardapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.berkeyilmaz.cardapp.data.local.converter.StringListConverter
import com.berkeyilmaz.cardapp.data.local.dao.InternalContactDAO
import com.berkeyilmaz.cardapp.data.local.dao.PhotoDAO
import com.berkeyilmaz.cardapp.data.local.entity.InternalContactEntity
import com.berkeyilmaz.cardapp.data.local.entity.PhotoEntity

@Database(
    entities = [PhotoEntity::class, InternalContactEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class CardConnectDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDAO
    abstract fun internalContactDao(): InternalContactDAO
}