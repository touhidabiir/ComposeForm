package com.touhid.composeform.feature.login.ui.otp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExtractOtpTest {

    @Test
    fun `finds the code ahead of the app hash`() {
        assertEquals("12345", extractOtp("<#> Your ComposeForm code is 12345\nFA+9qCX9VSu", 5))
    }

    @Test
    fun `ignores longer and shorter digit runs`() {
        assertEquals("54321", extractOtp("Ref 1234567, code 54321, valid 10 min", 5))
    }

    @Test
    fun `returns null when there is no code of that length`() {
        assertNull(extractOtp("Your code is 1234", 5))
        assertNull(extractOtp("", 5))
    }
}
