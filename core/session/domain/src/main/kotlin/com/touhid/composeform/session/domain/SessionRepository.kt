package com.touhid.composeform.session.domain

import com.touhid.composeform.model.AppResult

// Core slice rather than a feature: signing in, and later signing out or reacting to an expired
// session, is needed by more than one feature, so it can't live inside any one of them.
//
// Sign-in is phone + one-time password: requestOtp sends an SMS to the number, verifyOtp trades
// the code for a session.
interface SessionRepository {

    // Whether a session is stored (an access token exists). Doesn't call the server: an expired
    // token is still "signed in" here, and :core:network's TokenAuthenticator refreshes it on
    // the first 401.
    suspend fun isSignedIn(): Boolean

    // phoneNumber is the 11-digit local form (01XXXXXXXXX, see BangladeshPhoneNumber). appHash
    // is the app's SMS Retriever hash; the server appends it to the SMS so the app can read the
    // code without the SMS permission. Null when it couldn't be computed.
    suspend fun requestOtp(phoneNumber: String, appHash: String?): AppResult<Unit>

    // On success the tokens are already stored, so every later authenticated call just works.
    suspend fun verifyOtp(phoneNumber: String, otp: String): AppResult<UserSession>
}
