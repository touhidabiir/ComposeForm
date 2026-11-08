package com.touhid.composeform.feature.login.domain

import com.touhid.composeform.model.AppResult

// Sign-in is phone + one-time password: requestOtp sends an SMS to the number, verifyOtp trades
// the code for a session. The session itself (who is signed in, as which user type) belongs to
// :core:session; verifyOtp starts it there, and :app reads it.
interface LoginRepository {

    // phoneNumber is the 11-digit local form (01XXXXXXXXX, see BangladeshPhoneNumber). appHash
    // is the app's SMS Retriever hash; the server appends it to the SMS so the app can read the
    // code without the SMS permission. Null when it couldn't be computed.
    suspend fun requestOtp(phoneNumber: String, appHash: String?): AppResult<Unit>

    // On success the session has started: tokens and user stored, so every later authenticated
    // call just works.
    suspend fun verifyOtp(phoneNumber: String, otp: String): AppResult<Unit>
}
