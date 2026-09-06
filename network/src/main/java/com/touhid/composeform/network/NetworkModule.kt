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
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

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
    fun provideRetrofit(factory: RetrofitFactory, authInterceptor: AuthInterceptor, @BaseUrl baseUrl: String): Retrofit =
        factory.create(baseUrl = baseUrl, authInterceptor = authInterceptor)

    @Provides
    @Singleton
    fun provideAppApiService(retrofit: Retrofit): AppApiService = retrofit.create(AppApiService::class.java)

    @Provides
    @Singleton
    @PaymentRetrofit
    fun providePaymentRetrofit(factory: RetrofitFactory, authInterceptor: AuthInterceptor, @PaymentBaseUrl baseUrl: String): Retrofit =
        factory.create(baseUrl = baseUrl, authInterceptor = authInterceptor)

    @Provides
    @Singleton
    fun providePaymentApiService(@PaymentRetrofit retrofit: Retrofit): PaymentApiService =
        retrofit.create(PaymentApiService::class.java)

    @Provides
    @Singleton
    @AnalyticsRetrofit
    fun provideAnalyticsRetrofit(factory: RetrofitFactory, authInterceptor: AuthInterceptor, @AnalyticsBaseUrl baseUrl: String): Retrofit =
        factory.create(
            baseUrl = baseUrl,
            authInterceptor = authInterceptor,
            interceptors = listOf(HeaderInterceptor(mapOf("X-Client-Id" to "composeform-app"))),
        )

    @Provides
    @Singleton
    fun provideAnalyticsApiService(@AnalyticsRetrofit retrofit: Retrofit): AnalyticsApiService =
        retrofit.create(AnalyticsApiService::class.java)

    // Partner is a third-party backend, not ours - authInterceptor is omitted entirely (not
    // just conditionally skipped) so it can never receive our app's bearer token; it gets its
    // own API key header instead.
    @Provides
    @Singleton
    @PartnerRetrofit
    fun providePartnerRetrofit(factory: RetrofitFactory, @PartnerBaseUrl baseUrl: String): Retrofit =
        factory.create(
            baseUrl = baseUrl,
            authInterceptor = null,
            interceptors = listOf(HeaderInterceptor(mapOf("X-Api-Key" to "placeholder-partner-api-key"))),
        )

    @Provides
    @Singleton
    fun providePartnerApiService(@PartnerRetrofit retrofit: Retrofit): PartnerApiService =
        retrofit.create(PartnerApiService::class.java)
}
