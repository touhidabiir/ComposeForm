package com.touhid.composeform.session.domain

sealed interface SessionState {

    data class SignedIn(val user: UserSession) : SessionState

    // There was a session, but it can't be used any more: the refresh token was rejected (tokens
    // cleared, user still stored), or the user signed in on an earlier day. Sign in again.
    data object Ended : SessionState

    // Nobody has signed in, or the last user signed out.
    data object SignedOut : SessionState
}
