package com.touhid.composeform.feature.login.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

// The 11-character hash SMS Retriever matches an SMS against: the server ends the OTP SMS with
// it, and only then does Play services hand that SMS to this app (SmsOtpRetriever). It depends on
// the signing key, so debug and release builds each have their own; logged in debug builds so
// the backend can be configured with it.
// https://developers.google.com/identity/sms-retriever/verify#computing_your_apps_hash_string
@Singleton
class SmsAppHash @Inject constructor(@ApplicationContext private val context: Context) {

    val value: String? by lazy {
        runCatching { compute() }.getOrNull().also { hash ->
            if (BuildConfig.DEBUG) Log.d("SmsAppHash", "SMS Retriever app hash: $hash")
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    @Suppress("DEPRECATION")
    private fun compute(): String? {
        val packageName = context.packageName
        val packageManager = context.packageManager
        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                .signingInfo?.apkContentsSigners
        } else {
            packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
        }
        val signature = signatures?.firstOrNull() ?: return null
        val digest = MessageDigest.getInstance("SHA-256")
            .digest("$packageName ${signature.toCharsString()}".toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(digest.copyOfRange(0, 9), Base64.NO_PADDING or Base64.NO_WRAP).take(11)
    }
}
