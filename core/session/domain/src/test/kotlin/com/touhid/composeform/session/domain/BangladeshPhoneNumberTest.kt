package com.touhid.composeform.session.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BangladeshPhoneNumberTest {

    @Test
    fun `accepts every operator prefix 013 to 019`() {
        (3..9).forEach { operator ->
            assertTrue(BangladeshPhoneNumber.isValid("01${operator}12345678"))
        }
    }

    @Test
    fun `rejects operator digits 0 to 2`() {
        assertFalse(BangladeshPhoneNumber.isValid("01012345678"))
        assertFalse(BangladeshPhoneNumber.isValid("01112345678"))
        assertFalse(BangladeshPhoneNumber.isValid("01212345678"))
    }

    @Test
    fun `rejects wrong length, prefix or non-digits`() {
        assertFalse(BangladeshPhoneNumber.isValid(""))
        assertFalse(BangladeshPhoneNumber.isValid("0171234567"))
        assertFalse(BangladeshPhoneNumber.isValid("017123456789"))
        assertFalse(BangladeshPhoneNumber.isValid("+8801712345678"))
        assertFalse(BangladeshPhoneNumber.isValid("11712345678"))
        assertFalse(BangladeshPhoneNumber.isValid("0171234567a"))
    }

    @Test
    fun `formats the international form for display`() {
        assertEquals("+8801937049061", BangladeshPhoneNumber.toInternational("01937049061"))
    }
}
