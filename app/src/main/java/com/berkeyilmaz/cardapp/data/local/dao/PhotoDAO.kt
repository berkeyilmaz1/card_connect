package com.berkeyilmaz.cardapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.berkeyilmaz.cardapp.data.local.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDAO {
    @Query("SELECT * FROM photos WHERE contactId = :contactId AND userId = :userId")
    fun getPhotosByContactIdAndUserId(
        contactId: String, userId: String
    ): Flow<PhotoEntity>

    @Query("SELECT * FROM photos where userId= :userId")
    fun getAllPhotos(
        userId: String
    ): Flow<List<PhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhoto(photo: PhotoEntity)

    @Delete
    fun deletePhoto(photo: PhotoEntity)
}