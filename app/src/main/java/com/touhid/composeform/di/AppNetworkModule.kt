package com.touhid.composeform.di

import android.content.SharedPreferences
import com.touhid.composeform.network.qualifier.AnalyticsBaseUrl
import com.touhid.composeform.network.qualifier.BaseUrl
import com.touhid.composeform.network.qualifier.PartnerBaseUrl
import com.touhid.composeform.network.qualifier.PaymentBaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Base URLs are read from the app's one shared encrypted preferences store (prefs, injected -
// same instance AppPreferencesModule.kt provides to EncryptedTokenProvider) rather than a
// dedicated file of their own. Whatever writes them (a config/admin screen, a setup flow) is
// expected to do so before these @Provides functions are first resolved.
//
// Read once, here, when Hilt's SingletonComponent first resolves these - Retrofit/OkHttpClient
// (NetworkModule) are then built once as @Singletons from that value, so a base URL saved after
// that point takes effect on the next app/process restart, not live.
//
// Falls back to a placeholder URL when nothing has been saved yet (nothing in the app writes
// these keys today). The fallback must be a syntactically valid http(s) URL: an empty string
// becomes baseUrl("/") after RetrofitFactory's trailing-slash normalization, and Retrofit throws
// "Expected URL scheme 'http' or 'https'" the first time any repository is injected. The
// @RefreshRetrofit client is usually where that surfaces, only because Hilt builds it first
// (TokenAuthenticator needs it before provideRetrofit runs). In debug builds MockDataInterceptor
// answers the mocked paths regardless of host, so the placeholders are enough to run the app.
@Module
@InstallIn(SingletonComponent::class)
object AppNetworkModule {

    private const val KEY_BASE_URL = "base_url"
    private const val KEY_PAYMENT_BASE_URL = "payment_base_url"
    private const val KEY_ANALYTICS_BASE_URL = "analytics_base_url"
    private const val KEY_PARTNER_BASE_URL = "partner_base_url"

    private const val DEFAULT_BASE_URL = "https://api.composeform.dummy/"
    private const val DEFAULT_PAYMENT_BASE_URL = "https://payment.composeform.dummy/"
    private const val DEFAULT_ANALYTICS_BASE_URL = "https://analytics.composeform.dummy/"
    private const val DEFAULT_PARTNER_BASE_URL = "https://partner.composeform.dummy/"

    // ifBlank, not just getString's default - that default only covers a missing key, and a key
    // saved as "" (or whitespace) would crash Retrofit exactly the same way.
    private fun SharedPreferences.baseUrl(key: String, default: String): String =
        getString(key, null).orEmpty().trim().ifBlank { default }

    @Provides
    @BaseUrl
    fun provideBaseUrl(prefs: SharedPreferences): String = prefs.baseUrl(KEY_BASE_URL, DEFAULT_BASE_URL)

    @Provides
    @PaymentBaseUrl
    fun providePaymentBaseUrl(prefs: SharedPreferences): String = prefs.baseUrl(KEY_PAYMENT_BASE_URL, DEFAULT_PAYMENT_BASE_URL)

    @Provides
    @AnalyticsBaseUrl
    fun provideAnalyticsBaseUrl(prefs: SharedPreferences): String = prefs.baseUrl(KEY_ANALYTICS_BASE_URL, DEFAULT_ANALYTICS_BASE_URL)

    // Third-party backend, not ours - NetworkModule builds this base URL's Retrofit with
    // authInterceptor = null, so our app's bearer token is never attached here regardless of
    // where this URL comes from.
    @Provides
    @PartnerBaseUrl
    fun providePartnerBaseUrl(prefs: SharedPreferences): String = prefs.baseUrl(KEY_PARTNER_BASE_URL, DEFAULT_PARTNER_BASE_URL)
}
