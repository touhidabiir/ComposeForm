package com.touhid.composeform.network

import com.touhid.composeform.network.api.AnalyticsApiService
import com.touhid.composeform.network.api.AppApiService
import com.touhid.composeform.network.api.PartnerApiService
import com.touhid.composeform.network.api.PaymentApiService
import com.touhid.composeform.network.auth.AuthInterceptor
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

    // Shared by every Retrofit instance below - one OkHttpClient (one connection pool/dispatcher)
    // serving multiple base URLs, rather than one client per URL. AuthInterceptor still runs for
    // all of them; a service whose requests must never carry the bearer token (PartnerApiService)
    // opts out per-method via @NoAuth instead of needing its own client.
    private fun buildRetrofit(okHttpClient: OkHttpClient, baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, @BaseUrl baseUrl: String): Retrofit =
        buildRetrofit(okHttpClient, baseUrl)

    @Provides
    @Singleton
    fun provideAppApiService(retrofit: Retrofit): AppApiService = retrofit.create(AppApiService::class.java)

    @Provides
    @Singleton
    @PaymentRetrofit
    fun providePaymentRetrofit(okHttpClient: OkHttpClient, @PaymentBaseUrl baseUrl: String): Retrofit =
        buildRetrofit(okHttpClient, baseUrl)

    @Provides
    @Singleton
    fun providePaymentApiService(@PaymentRetrofit retrofit: Retrofit): PaymentApiService =
        retrofit.create(PaymentApiService::class.java)

    @Provides
    @Singleton
    @AnalyticsRetrofit
    fun provideAnalyticsRetrofit(okHttpClient: OkHttpClient, @AnalyticsBaseUrl baseUrl: String): Retrofit =
        buildRetrofit(okHttpClient, baseUrl)

    @Provides
    @Singleton
    fun provideAnalyticsApiService(@AnalyticsRetrofit retrofit: Retrofit): AnalyticsApiService =
        retrofit.create(AnalyticsApiService::class.java)

    @Provides
    @Singleton
    @PartnerRetrofit
    fun providePartnerRetrofit(okHttpClient: OkHttpClient, @PartnerBaseUrl baseUrl: String): Retrofit =
        buildRetrofit(okHttpClient, baseUrl)

    @Provides
    @Singleton
    fun providePartnerApiService(@PartnerRetrofit retrofit: Retrofit): PartnerApiService =
        retrofit.create(PartnerApiService::class.java)
}
