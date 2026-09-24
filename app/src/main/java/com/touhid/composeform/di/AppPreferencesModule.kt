package com.touhid.composeform.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// The app's one shared preferences store - originally just the auth token
// (EncryptedTokenProvider), now also the four base URLs (AppNetworkModule), and expected to hold
// more general app-local state over time. Encrypted (not plain SharedPreferences) even though not
// every value stored in it is a secret - EncryptedSharedPreferences encrypts every entry in a
// given file uniformly, there's no way to mix plain and encrypted entries within one file, so
// consolidating into a single store means the non-secret values pay the same MasterKey/crypto
// overhead the token needs. Deliberate: one shared file was chosen over one encrypted file for
// secrets plus a separate plain file for everything else.
@Module
@InstallIn(SingletonComponent::class)
object AppPreferencesModule {

    private const val PREFS_FILE_NAME = "app_prefs"

    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}
