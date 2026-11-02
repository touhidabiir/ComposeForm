package com.touhid.composeform.session.domain

// The signed-in user. The access/refresh tokens are deliberately not here: SessionRepository keeps
// them in :core:network's TokenProvider, so no caller ever handles a token by hand.
data class UserSession(
    val userId: String,
    val name: String,
    val userType: UserType,
)
