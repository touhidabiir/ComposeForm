package com.touhid.composeform.di

import com.touhid.composeform.BuildConfig
import com.touhid.composeform.network.qualifier.AppVersionCode
import com.touhid.composeform.network.qualifier.BaseUrl
import com.touhid.composeform.network.qualifier.MockEnabled
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// The base URL is the build flavor's (sit, uat or prod - see app/build.gradle.kts), so :core:network
// never knows which environment it talks to. The values are placeholders until the real backends
// exist; in a flavor with IS_MOCK_ENABLED, MockDataInterceptor answers the mocked paths regardless
// of host, so they are enough to run the app. Each must be a syntactically valid http(s) URL: an empty string
// becomes baseUrl("/") after RetrofitFactory's trailing-slash normalization, and Retrofit throws
// "Expected URL scheme 'http' or 'https'" the first time any repository is injected.
@Module
@InstallIn(SingletonComponent::class)
object AppNetworkModule {

    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    // Whether :core:network serves mocked responses: the flavor's IS_MOCK_ENABLED. Only sit and
    // uat include :core:network-mock, so only they may set it to true.
    @Provides
    @MockEnabled
    fun provideMockEnabled(): Boolean = BuildConfig.IS_MOCK_ENABLED

    // Sent on every request as "X-Version: android/<versionCode>". Comes from this module's own
    // BuildConfig because only the application module has a versionCode.
    @Provides
    @AppVersionCode
    fun provideAppVersionCode(): Int = BuildConfig.VERSION_CODE
}
