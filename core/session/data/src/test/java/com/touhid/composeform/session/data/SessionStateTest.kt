package com.touhid.composeform.session.data

import com.touhid.composeform.session.domain.SessionState
import com.touhid.composeform.session.domain.UserSession
import com.touhid.composeform.session.domain.UserType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class SessionStateTest {

    private val user = UserSession(userId = "1", name = "Demo", userType = UserType.Agent)

    @Test
    fun `nothing stored is signed out`() {
        assertEquals(SessionState.SignedOut, sessionState(null, hasToken = false, signedInOn = null, today = "2026-11-02"))
    }

    @Test
    fun `user and token from today is signed in`() {
        assertEquals(
            SessionState.SignedIn(user),
            sessionState(user, hasToken = true, signedInOn = "2026-11-02", today = "2026-11-02"),
        )
    }

    @Test
    fun `a session from an earlier day has ended`() {
        assertEquals(SessionState.Ended, sessionState(user, hasToken = true, signedInOn = "2026-11-01", today = "2026-11-02"))
    }

    @Test
    fun `tokens cleared by the authenticator ends the session`() {
        assertEquals(SessionState.Ended, sessionState(user, hasToken = false, signedInOn = "2026-11-02", today = "2026-11-02"))
    }

    @Test
    fun `a token without a stored user has ended`() {
        assertEquals(SessionState.Ended, sessionState(null, hasToken = true, signedInOn = null, today = "2026-11-02"))
    }

    @Test
    fun `the day changes at midnight Bangladesh time, not UTC`() {
        // 2026-11-01T18:30Z is 00:30 on 2 November in Dhaka (UTC+6).
        assertEquals("2026-11-02", todayInBangladesh(Date(1_793_557_800_000L)))
    }
}
