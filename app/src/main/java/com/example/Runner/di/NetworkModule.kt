package com.example.Runner.di

import com.example.Runner.data.api.GeminiApi
import com.example.Runner.data.api.GeminiApiService
import com.example.Runner.data.api.GoogleDirectionsApi
import com.example.Runner.data.api.GoogleDirectionsApiService
import com.example.Runner.data.repository.ClaudeService
import com.example.Runner.data.repository.RouteApiService
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("GoogleMapsRetrofit")
    fun provideGoogleMapsRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    @Named("GeminiRetrofit")
    fun provideGeminiRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleDirectionsApi(@Named("GoogleMapsRetrofit") retrofit: Retrofit): GoogleDirectionsApi {
        return retrofit.create(GoogleDirectionsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGeminiApi(@Named("GeminiRetrofit") retrofit: Retrofit): GeminiApi {
        return retrofit.create(GeminiApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRouteApiService(
        googleDirectionsApiService: GoogleDirectionsApiService
    ): RouteApiService {
        return googleDirectionsApiService
    }

    @Provides
    @Singleton
    fun provideClaudeService(
        geminiApiService: GeminiApiService
    ): ClaudeService {
        return geminiApiService
    }
}