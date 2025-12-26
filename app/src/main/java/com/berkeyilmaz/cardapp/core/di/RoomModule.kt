package com.berkeyilmaz.cardapp.core.di

import android.content.Context
import androidx.room.Room
import com.berkeyilmaz.cardapp.data.local.dao.PhotoDAO
import com.berkeyilmaz.cardapp.data.local.db.CardConnectDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): CardConnectDatabase {
        return Room.databaseBuilder(
            context,
            CardConnectDatabase::class.java,
            "app_db"
        ).build()
    }

    @Provides
    fun providePhotoDao(db: CardConnectDatabase): PhotoDAO = db.photoDao()
}