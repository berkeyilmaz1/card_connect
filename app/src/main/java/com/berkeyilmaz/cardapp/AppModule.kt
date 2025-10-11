package com.berkeyilmaz.cardapp


import android.content.Context
import androidx.credentials.CredentialManager
import com.berkeyilmaz.cardapp.data.remote.AuthRepositoryImpl
import com.berkeyilmaz.cardapp.domain.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideCredentialManager(
        @ApplicationContext context: Context
    ): CredentialManager {
        return CredentialManager.create(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        firebaseAuth: FirebaseAuth,
        credentialManager: CredentialManager
    ): AuthRepository = AuthRepositoryImpl(
        context = context,
        firebaseAuth = firebaseAuth,
        credentialManager = credentialManager
    )
}
