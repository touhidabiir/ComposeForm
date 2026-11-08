package com.touhid.composeform.session.data

import android.content.SharedPreferences
import com.touhid.composeform.network.auth.TokenProvider
import com.touhid.composeform.session.domain.SessionRepository
import com.touhid.composeform.session.domain.SessionState
import com.touhid.composeform.session.domain.UserSession
import com.touhid.composeform.session.domain.UserType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

// Storage is :app's, same deferral as TokenProvider: prefs is the app's one shared encrypted
// store (:app's AppPreferencesModule), which also holds the tokens. Reads and writes run on IO,
// since EncryptedSharedPreferences can touch disk and decrypt.
internal class SessionRepositoryImpl @Inject constructor(
    private val prefs: SharedPreferences,
    private val tokenProvider: TokenProvider,
) : SessionRepository {

    override suspend fun state(): SessionState = withContext(Dispatchers.IO) {
        sessionState(
            user = readUser(),
            hasToken = !tokenProvider.getToken().isNullOrBlank(),
            signedInOn = prefs.getString(KEY_SIGNED_IN_ON, null),
            today = todayInBangladesh(),
        )
    }

    override suspend fun startSession(user: UserSession, accessToken: String, refreshToken: String) {
        withContext(Dispatchers.IO) {
            tokenProvider.setToken(accessToken)
            tokenProvider.setRefreshToken(refreshToken)
            // commit, not apply: the caller moves on to a home screen that relies on this.
            prefs.edit()
                .putString(KEY_USER_ID, user.userId)
                .putString(KEY_NAME, user.name)
                .putString(KEY_USER_TYPE, user.userType.name)
                .putString(KEY_SIGNED_IN_ON, todayInBangladesh())
                .commit()
        }
    }

    override suspend fun signOut() {
        withContext(Dispatchers.IO) {
            tokenProvider.clear()
            prefs.edit()
                .remove(KEY_USER_ID)
                .remove(KEY_NAME)
                .remove(KEY_USER_TYPE)
                .remove(KEY_SIGNED_IN_ON)
                .commit()
        }
    }

    private fun readUser(): UserSession? {
        val userId = prefs.getString(KEY_USER_ID, null) ?: return null
        val name = prefs.getString(KEY_NAME, null) ?: return null
        val userType = prefs.getString(KEY_USER_TYPE, null)
            ?.let { stored -> UserType.entries.firstOrNull { it.name == stored } }
            ?: return null
        return UserSession(userId = userId, name = name, userType = userType)
    }

    private companion object {
        const val KEY_USER_ID = "session_user_id"
        const val KEY_NAME = "session_name"
        const val KEY_USER_TYPE = "session_user_type"
        const val KEY_SIGNED_IN_ON = "session_signed_in_on"
    }
}

// The session rules, apart from storage so they can be unit tested.
internal fun sessionState(user: UserSession?, hasToken: Boolean, signedInOn: String?, today: String): SessionState =
    when {
        user == null && !hasToken -> SessionState.SignedOut
        // Only one of the two left: TokenAuthenticator cleared the tokens after the refresh token
        // was rejected, or the tokens predate stored users.
        user == null || !hasToken -> SessionState.Ended
        signedInOn != today -> SessionState.Ended
        else -> SessionState.SignedIn(user)
    }

// "2026-11-02". minSdk 24 has no java.time without desugaring, hence SimpleDateFormat.
internal fun todayInBangladesh(now: Date = Date()): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US)
        .apply { timeZone = TimeZone.getTimeZone("Asia/Dhaka") }
        .format(now)
