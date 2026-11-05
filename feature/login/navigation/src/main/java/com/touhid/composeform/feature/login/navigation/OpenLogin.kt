package com.touhid.composeform.feature.login.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

// Opens the sign-in flow (phone number, then OTP) and reports whether the user signed in: true
// once the OTP is verified and the session stored, false when they backed out.
//
// A result contract rather than a Destination: the caller (:app's splash) has to know how sign-in
// ended to decide between showing home and closing the app, and sign-in itself never needs to
// know what comes after it.
class OpenLogin(private val intents: LoginIntentFactory) : ActivityResultContract<Unit, Boolean>() {

    override fun createIntent(context: Context, input: Unit): Intent = intents.create(context)

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean = resultCode == Activity.RESULT_OK
}

// Builds the Intent for the sign-in Activity. :feature:login:ui binds the implementation with
// Hilt, so only that module knows which Activity backs sign-in; callers inject this and hand it
// to OpenLogin.
fun interface LoginIntentFactory {
    fun create(context: Context): Intent
}
