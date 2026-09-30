package com.touhid.composeform.network

import com.touhid.composeform.network.api.AnalyticsApiService
import com.touhid.composeform.network.api.AppApiService
import com.touhid.composeform.network.api.PartnerApiService
import com.touhid.composeform.network.api.PaymentApiService
import com.touhid.composeform.network.api.RefreshTokenApiService
import com.touhid.composeform.network.auth.AuthInterceptor
import com.touhid.composeform.network.auth.TokenAuthenticator
import com.touhid.composeform.network.interceptor.ErrorInterceptor
import com.touhid.composeform.network.interceptor.HeaderInterceptor
import com.touhid.composeform.network.qualifier.AnalyticsBaseUrl
import com.touhid.composeform.network.qualifier.AnalyticsRetrofit
import com.touhid.composeform.network.qualifier.BaseUrl
import com.touhid.composeform.network.qualifier.PartnerBaseUrl
import com.touhid.composeform.network.qualifier.PartnerRetrofit
import com.touhid.composeform.network.qualifier.PaymentBaseUrl
import com.touhid.composeform.network.qualifier.PaymentRetrofit
import com.touhid.composeform.network.qualifier.RefreshRetrofit
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

    // errorInterceptor is scoped to just this base URL, not passed to RetrofitFactory
    // unconditionally the way requestIdInterceptor is - its {message, status} error-body parsing
    // is this app's own backend's shape, not something Payment/Analytics/Partner (unrelated
    // backends) are guaranteed to return the same way. tokenAuthenticator is likewise scoped to
    // just this base URL - it refreshes this backend's own tokens.
    @Provides
    @Singleton
    fun provideRetrofit(
        factory: RetrofitFactory,
        authInterceptor: AuthInterceptor,
        errorInterceptor: ErrorInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        @BaseUrl baseUrl: String,
    ): Retrofit = factory.create(
        baseUrl = baseUrl,
        authInterceptor = authInterceptor,
        interceptors = listOf(errorInterceptor),
        authenticator = tokenAuthenticator,
    )

    @Provides
    @Singleton
    fun provideAppApiService(retrofit: Retrofit): AppApiService = retrofit.create(AppApiService::class.java)

    // Same backend as provideRetrofit, but a bare client: no authInterceptor (the refresh call
    // authenticates with the refresh token in its body, never the expired access token) and no
    // authenticator (a 401 here means the refresh token itself was rejected, which
    // TokenAuthenticator handles by ending the session - never by recursing into itself).
    @Provides
    @Singleton
    @RefreshRetrofit
    fun provideRefreshRetrofit(factory: RetrofitFactory, @BaseUrl baseUrl: String): Retrofit =
        factory.create(baseUrl = baseUrl, authInterceptor = null)

    @Provides
    @Singleton
    fun provideRefreshTokenApiService(@RefreshRetrofit retrofit: Retrofit): RefreshTokenApiService =
        retrofit.create(RefreshTokenApiService::class.java)

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
