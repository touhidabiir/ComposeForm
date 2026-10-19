package com.touhid.composeform.network

import com.touhid.composeform.network.api.RefreshTokenApiService
import com.touhid.composeform.network.auth.AuthInterceptor
import com.touhid.composeform.network.auth.TokenAuthenticator
import com.touhid.composeform.network.interceptor.ErrorInterceptor
import com.touhid.composeform.network.qualifier.BaseUrl
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

    // The main @BaseUrl client. Every feature/core-slice data module creates its own Retrofit
    // service from this instance. errorInterceptor is passed here rather than added by
    // RetrofitFactory unconditionally the way requestIdInterceptor is - its {message, status}
    // error-body parsing is this backend's own shape, which another backend added later isn't
    // guaranteed to share. tokenAuthenticator is likewise this backend's: it refreshes its tokens.
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
}
