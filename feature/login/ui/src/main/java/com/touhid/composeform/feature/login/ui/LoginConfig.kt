package com.touhid.composeform.feature.login.ui

import kotlin.time.Duration.Companion.minutes

// Placeholders until the real values exist.
internal object LoginConfig {
    const val PRIVACY_POLICY_URL = "https://composeform.dummy/privacy-policy"
    const val SUPPORT_HOTLINE = "096 00 000 000"

    const val OTP_LENGTH = 5

    // How long the OTP screen waits for the SMS: the bar under its title fills over this time,
    // and resending a code is possible only once it's full.
    val OTP_WAIT = 10.minutes
}
