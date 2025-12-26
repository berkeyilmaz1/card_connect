package com.berkeyilmaz.cardapp.core.di

import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MLKitModule {

    @Provides
    @Singleton
    fun provideTextRecognizer(): TextRecognizer = TextRecognition.getClient(
        TextRecognizerOptions.Builder().build()
    )
}
