package com.touhid.composeform.di

import android.content.SharedPreferences
import com.touhid.composeform.network.qualifier.BaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// The base URL is read from the app's one shared encrypted preferences store (prefs, injected -
// same instance AppPreferencesModule.kt provides to EncryptedTokenProvider) rather than a
// dedicated file of its own. Whatever writes it (a config/admin screen, a setup flow) is
// expected to do so before this @Provides function is first resolved.
//
// Read once, here, when Hilt's SingletonComponent first resolves it - Retrofit/OkHttpClient
// (NetworkModule) are then built once as @Singletons from that value, so a base URL saved after
// that point takes effect on the next app/process restart, not live.
//
// Falls back to a placeholder URL when nothing has been saved yet (nothing in the app writes
// this key today). The fallback must be a syntactically valid http(s) URL: an empty string
// becomes baseUrl("/") after RetrofitFactory's trailing-slash normalization, and Retrofit throws
// "Expected URL scheme 'http' or 'https'" the first time any repository is injected. The
// @RefreshRetrofit client is usually where that surfaces, only because Hilt builds it first
// (TokenAuthenticator needs it before provideRetrofit runs). In debug builds MockDataInterceptor
// answers the mocked paths regardless of host, so the placeholder is enough to run the app.
@Module
@InstallIn(SingletonComponent::class)
object AppNetworkModule {

    private const val KEY_BASE_URL = "base_url"

    private const val DEFAULT_BASE_URL = "https://api.composeform.dummy/"

    // ifBlank, not just getString's default - that default only covers a missing key, and a key
    // saved as "" (or whitespace) would crash Retrofit exactly the same way.
    private fun SharedPreferences.baseUrl(key: String, default: String): String =
        getString(key, null).orEmpty().trim().ifBlank { default }

    @Provides
    @BaseUrl
    fun provideBaseUrl(prefs: SharedPreferences): String = prefs.baseUrl(KEY_BASE_URL, DEFAULT_BASE_URL)
}
