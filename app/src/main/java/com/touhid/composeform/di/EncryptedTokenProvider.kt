package com.touhid.composeform.di

import android.content.SharedPreferences
import com.touhid.composeform.network.auth.TokenProvider
import javax.inject.Inject
import javax.inject.Singleton

// prefs is the app's one shared encrypted preferences store (AppPreferencesModule.kt) - this
// class no longer builds its own EncryptedSharedPreferences instance/file.
@Singleton
class EncryptedTokenProvider @Inject constructor(
    private val prefs: SharedPreferences,
) : TokenProvider {

    override fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    override fun setToken(token: String?) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    private companion object {
        const val KEY_TOKEN = "auth_token"
    }
}
