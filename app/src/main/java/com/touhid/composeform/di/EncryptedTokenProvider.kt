package com.touhid.composeform.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.touhid.composeform.network.auth.TokenProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedTokenProvider @Inject constructor(
    @ApplicationContext context: Context
) : TokenProvider {

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    override fun setToken(token: String?) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    private companion object {
        const val PREFS_FILE_NAME = "auth_token_prefs"
        const val KEY_TOKEN = "auth_token"
    }
}
