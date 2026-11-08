package com.touhid.composeform.session.domain

// The signed-in session: who is signed in, as which UserType, and since which day. A core slice
// because it's written by sign-in (:feature:login:data) and read by :app (the splash, which picks
// the home screen, and the session guard, which ends a session that's no longer valid).
//
// A session lasts for the day it started on (Bangladesh time): the first use on a later day has
// to sign in again.
interface SessionRepository {

    suspend fun state(): SessionState

    // Stores the tokens and the user, and records today as the day the session started.
    suspend fun startSession(user: UserSession, accessToken: String, refreshToken: String)

    // Forgets the tokens and the user.
    suspend fun signOut()
}
