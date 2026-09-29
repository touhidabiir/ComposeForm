package com.touhid.composeform.network.auth

/**
 * Storage for the auth tokens is a consuming module's concern (same deferral pattern as
 * `@BaseUrl`) - `:network` only depends on this contract to attach `Authorization` headers and
 * to rotate the access token via [TokenAuthenticator] once it expires.
 */
interface TokenProvider {
    fun getToken(): String?
    fun setToken(token: String?)
    fun getRefreshToken(): String?
    fun setRefreshToken(refreshToken: String?)

    fun clear() {
        setToken(null)
        setRefreshToken(null)
    }
}
