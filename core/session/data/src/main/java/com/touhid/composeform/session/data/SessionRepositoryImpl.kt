package com.touhid.composeform.session.data

import com.touhid.composeform.model.AppResult
import com.touhid.composeform.model.map
import com.touhid.composeform.network.auth.TokenProvider
import com.touhid.composeform.network.safeAppCall
import com.touhid.composeform.session.domain.SessionRepository
import com.touhid.composeform.session.domain.UserSession
import javax.inject.Inject

internal class SessionRepositoryImpl @Inject constructor(
    private val api: SessionApi,
    private val tokenProvider: TokenProvider,
) : SessionRepository {

    override suspend fun login(username: String, password: String): AppResult<UserSession> =
        safeAppCall { api.login(LoginRequestDto(username, password)) }.map { response ->
            tokenProvider.setToken(response.token)
            tokenProvider.setRefreshToken(response.refreshToken)
            UserSession(userId = response.userId, name = response.name, role = response.role)
        }
}
