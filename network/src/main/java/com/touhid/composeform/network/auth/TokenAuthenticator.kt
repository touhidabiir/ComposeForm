package com.touhid.composeform.network.auth

import com.touhid.composeform.network.api.RefreshTokenApiService
import com.touhid.composeform.network.interceptor.REQUEST_ID_HEADER
import com.touhid.composeform.network.model.RefreshTokenRequest
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// Invoked by OkHttp whenever a request on the main @BaseUrl client comes back 401: refreshes the
// access token with the stored refresh token and retries the original request once with the new
// one. Returning null gives up, and the original 401 then continues on up to ErrorInterceptor/
// safeApiCall as a normal NetworkError.Http failure.
//
// refreshApi is served by its own Retrofit (NetworkModule's @RefreshRetrofit) with no
// AuthInterceptor and no authenticator, so a 401 from the refresh call itself can't recurse back
// into this class.
@Singleton
internal class TokenAuthenticator @Inject constructor(
    private val tokenProvider: TokenProvider,
    private val refreshApi: RefreshTokenApiService,
    private val sessionEvents: AuthSessionEvents,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // The request never carried a token (e.g. login with bad credentials) - there's nothing
        // to refresh, the 401 is the real answer.
        val failedToken = response.request.header(AUTHORIZATION_HEADER)?.removePrefix(BEARER_PREFIX) ?: return null
        if (responseCount(response) >= MAX_ATTEMPTS) return null

        // Serialized so parallel requests that all 401 together trigger one refresh, not one
        // each - the refresh token is rotated on use, so a second concurrent refresh would be
        // sent an already-invalidated refresh token and log the user out.
        synchronized(this) {
            // Already logged out by an earlier refresh failure while this request was waiting.
            val currentToken = tokenProvider.getToken() ?: return null

            // Another request refreshed the token while this one was in flight or waiting on the
            // lock - just retry with the new one.
            if (currentToken != failedToken) return response.request.withToken(currentToken)

            val refreshToken = tokenProvider.getRefreshToken()
            if (refreshToken.isNullOrEmpty()) {
                expireSession()
                return null
            }

            val refreshResponse = try {
                refreshApi.refresh(RefreshTokenRequest(refreshToken)).execute()
            } catch (e: IOException) {
                // A connectivity failure says nothing about the refresh token's validity - keep
                // the session, the caller just sees this request's 401.
                return null
            }

            val body = refreshResponse.body()
            if (refreshResponse.isSuccessful && body != null) {
                tokenProvider.setToken(body.token)
                tokenProvider.setRefreshToken(body.refreshToken)
                return response.request.withToken(body.token)
            }

            // Only an explicit rejection of the refresh token ends the session - a 5xx is the
            // backend's problem, not proof the user needs to sign in again.
            if (refreshResponse.code() == 401 || refreshResponse.code() == 403) {
                expireSession()
            }
            return null
        }
    }

    // header() rather than addHeader() so both values are replaced, not duplicated - the original
    // request already carries the old Authorization and X-Request-Id headers. The request ID is
    // regenerated since the retry is a separate request as far as the backend is concerned.
    private fun Request.withToken(token: String): Request = newBuilder()
        .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
        .header(AUTHORIZATION_HEADER, "$BEARER_PREFIX$token")
        .build()

    private fun responseCount(response: Response): Int = generateSequence(response) { it.priorResponse }.count()

    private fun expireSession() {
        tokenProvider.clear()
        sessionEvents.notifySessionExpired()
    }

    private companion object {
        // The original attempt plus one retry with a refreshed token.
        const val MAX_ATTEMPTS = 2
    }
}
