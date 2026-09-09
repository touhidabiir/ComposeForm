package com.touhid.composeform.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.touhid.composeform.network.api.AppApiService
import com.touhid.composeform.network.auth.AuthInterceptor
import com.touhid.composeform.network.model.LeadStatus
import com.touhid.composeform.network.model.LeadStatusTypeAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val TIMEOUT_SECONDS = 30L

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        // TODO: remove once the real backend is live - also delete MockDataInterceptor.kt and
        // MockJson.kt (network/) at the same time, they exist solely to back this call. Debug-gated
        // the same way provideLoggingInterceptor() above is, so a release build can never end up
        // silently serving fake data instead of failing to reach a real backend.
        .apply { if (BuildConfig.DEBUG) addInterceptor(MockDataInterceptor()) }
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(LeadStatus::class.java, LeadStatusTypeAdapter)
        .create()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, @BaseUrl baseUrl: String, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAppApiService(retrofit: Retrofit): AppApiService = retrofit.create(AppApiService::class.java)
}
