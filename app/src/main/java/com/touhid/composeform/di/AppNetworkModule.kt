package com.touhid.composeform.di

import android.content.Context
import com.touhid.composeform.network.qualifier.AnalyticsBaseUrl
import com.touhid.composeform.network.qualifier.BaseUrl
import com.touhid.composeform.network.qualifier.PartnerBaseUrl
import com.touhid.composeform.network.qualifier.PaymentBaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

// Base URLs are read from plain SharedPreferences - not EncryptedSharedPreferences the way the
// auth token is (EncryptedTokenProvider.kt), since a base URL isn't a secret and doesn't need the
// MasterKey/crypto overhead on every cold start. Whatever writes them (a config/admin screen, a
// setup flow) is expected to do so before these @Provides functions are first resolved.
//
// Read once, here, when Hilt's SingletonComponent first resolves these - Retrofit/OkHttpClient
// (NetworkModule) are then built once as @Singletons from that value, so a base URL saved after
// that point takes effect on the next app/process restart, not live. Defaults to "" when unset.
// Note this means Retrofit.Builder().baseUrl("") - via RetrofitFactory's trailing-slash
// normalization, effectively baseUrl("/") - will throw IllegalArgumentException the first time
// any of these are actually resolved (i.e. the first screen that injects AppRepository or an
// equivalent repository) if a real URL hasn't been saved yet. Acceptable for now since this is a
// deliberate simplification, not an oversight - flagging it here since it's the one sharp edge of
// keeping "default to empty" this simple.
@Module
@InstallIn(SingletonComponent::class)
object AppNetworkModule {

    private const val PREFS_FILE_NAME = "base_url_prefs"
    private const val KEY_BASE_URL = "base_url"
    private const val KEY_PAYMENT_BASE_URL = "payment_base_url"
    private const val KEY_ANALYTICS_BASE_URL = "analytics_base_url"
    private const val KEY_PARTNER_BASE_URL = "partner_base_url"

    private fun Context.readBaseUrl(key: String): String =
        getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE).getString(key, "").orEmpty()

    @Provides
    @BaseUrl
    fun provideBaseUrl(@ApplicationContext context: Context): String = context.readBaseUrl(KEY_BASE_URL)

    @Provides
    @PaymentBaseUrl
    fun providePaymentBaseUrl(@ApplicationContext context: Context): String = context.readBaseUrl(KEY_PAYMENT_BASE_URL)

    @Provides
    @AnalyticsBaseUrl
    fun provideAnalyticsBaseUrl(@ApplicationContext context: Context): String = context.readBaseUrl(KEY_ANALYTICS_BASE_URL)

    // Third-party backend, not ours - NetworkModule builds this base URL's Retrofit with
    // authInterceptor = null, so our app's bearer token is never attached here regardless of
    // where this URL comes from.
    @Provides
    @PartnerBaseUrl
    fun providePartnerBaseUrl(@ApplicationContext context: Context): String = context.readBaseUrl(KEY_PARTNER_BASE_URL)
}
