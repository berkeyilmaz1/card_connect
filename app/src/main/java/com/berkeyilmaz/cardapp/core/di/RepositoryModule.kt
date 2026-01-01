package com.berkeyilmaz.cardapp.core.di

import com.berkeyilmaz.cardapp.data.local.LanguageRepositoryImpl
import com.berkeyilmaz.cardapp.data.local.LlmRepositoryImpl
import com.berkeyilmaz.cardapp.data.local.LocalLlmModelRepositoryImpl
import com.berkeyilmaz.cardapp.data.local.PhotoRepositoryImpl
import com.berkeyilmaz.cardapp.data.local.ThemeRepositoryImpl
import com.berkeyilmaz.cardapp.data.remote.AuthRepositoryImpl
import com.berkeyilmaz.cardapp.data.remote.ContactRepositoryImpl
import com.berkeyilmaz.cardapp.data.remote.HomeRepositoryImpl
import com.berkeyilmaz.cardapp.data.remote.ScanRepositoryImpl
import com.berkeyilmaz.cardapp.domain.LanguageRepository
import com.berkeyilmaz.cardapp.domain.auth.AuthRepository
import com.berkeyilmaz.cardapp.domain.contact.ContactRepository
import com.berkeyilmaz.cardapp.domain.home.HomeRepository
import com.berkeyilmaz.cardapp.domain.photo.repository.PhotoRepository
import com.berkeyilmaz.cardapp.domain.scan.ScanRepository
import com.berkeyilmaz.cardapp.domain.settings.LlmRepository
import com.berkeyilmaz.cardapp.domain.settings.LocalLlmModelRepository
import com.berkeyilmaz.cardapp.domain.settings.ThemeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindThemeRepository(
        themeRepositoryImpl: ThemeRepositoryImpl
    ): ThemeRepository

    @Binds
    @Singleton
    abstract fun bindLlmRepository(
        llmRepositoryImpl: LlmRepositoryImpl
    ): LlmRepository

    @Binds
    @Singleton
    abstract fun bindLocalLlmModelRepository(
        localLlmModelRepositoryImpl: LocalLlmModelRepositoryImpl
    ): LocalLlmModelRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindLanguageRepository(
        languageRepositoryImpl: LanguageRepositoryImpl
    ): LanguageRepository

    @Binds
    @Singleton
    abstract fun bindContactRepository(
        contactRepositoryImpl: ContactRepositoryImpl
    ): ContactRepository

    @Binds
    @Singleton
    abstract fun bindScanRepository(
        scanRepositoryImpl: ScanRepositoryImpl
    ): ScanRepository

    @Binds
    @Singleton
    abstract fun bindPhotoRepository(
        impl: PhotoRepositoryImpl
    ): PhotoRepository
}