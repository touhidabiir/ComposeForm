package com.touhid.composeform.feature.login.ui.otp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.os.BundleCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

// Listens for the OTP SMS with Google's SMS Retriever API while it's in composition: no SMS
// permission and no prompt, but Play services only delivers an SMS that ends with this app's
// hash (SmsAppHash), so the server has to append it. A new session restarts listening (after a
// resend). SMS Retriever gives up after 5 minutes; listening restarts then, since the screen
// waits longer than that.
@Composable
internal fun SmsOtpRetriever(session: Int, otpLength: Int, onOtpReceived: (String) -> Unit) {
    val context = LocalContext.current
    val currentOnOtpReceived by rememberUpdatedState(onOtpReceived)

    DisposableEffect(context, session) {
        val client = SmsRetriever.getClient(context)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(receiverContext: Context, intent: Intent) {
                if (intent.action != SmsRetriever.SMS_RETRIEVED_ACTION) return
                val extras = intent.extras ?: return
                val status = BundleCompat.getParcelable(extras, SmsRetriever.EXTRA_STATUS, Status::class.java) ?: return
                when (status.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE).orEmpty()
                        extractOtp(message, otpLength)?.let(currentOnOtpReceived)
                    }
                    CommonStatusCodes.TIMEOUT -> client.startSmsRetriever()
                }
            }
        }
        // Exported, but only Play services can send it: the broadcast requires its permission.
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
            SmsRetriever.SEND_PERMISSION,
            null,
            ContextCompat.RECEIVER_EXPORTED,
        )
        client.startSmsRetriever()
        onDispose { context.unregisterReceiver(receiver) }
    }
}

// The first run of exactly otpLength digits in the SMS ("Your code is 12345 FA+9qCX9VSu" -> 12345).
internal fun extractOtp(message: String, otpLength: Int): String? =
    Regex("(?<!\\d)\\d{$otpLength}(?!\\d)").find(message)?.value
