package com.berkeyilmaz.cardapp


import com.berkeyilmaz.cardapp.data.remote.AuthApi
import com.berkeyilmaz.cardapp.data.remote.AuthRepositoryImpl
import com.berkeyilmaz.cardapp.domain.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthApi(okHttpClient: OkHttpClient): AuthApi =
        Retrofit.Builder().baseUrl("http://192.168.1.103:8080/").client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build().create(AuthApi::class.java)


    @Provides
    @Singleton
    fun provideOkHttpClient(
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder().addInterceptor(logging).build()
    }


    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi
    ): AuthRepository = AuthRepositoryImpl(api)
}
