package com.touhid.composeform.session.domain

import com.touhid.composeform.model.AppResult

// Core slice rather than a feature: signing in, and later signing out or reacting to an expired
// session, is needed by more than one feature, so it can't live inside any one of them.
interface SessionRepository {

    // On success the tokens are already stored, so every later authenticated call just works.
    suspend fun login(username: String, password: String): AppResult<UserSession>
}
