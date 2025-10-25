package com.berkeyilmaz.cardapp.di

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.berkeyilmaz.cardapp.data.local.ThemeRepositoryImpl
import com.berkeyilmaz.cardapp.data.remote.AuthRepositoryImpl
import com.berkeyilmaz.cardapp.data.remote.HomeRepositoryImpl
import com.berkeyilmaz.cardapp.domain.auth.AuthRepository
import com.berkeyilmaz.cardapp.domain.home.HomeRepository
import com.berkeyilmaz.cardapp.domain.theme.ThemeRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "app_preferences"
    )

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.dataStore

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideCredentialManager(
        @ApplicationContext context: Context
    ): CredentialManager = CredentialManager.create(context)
}

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
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository
}