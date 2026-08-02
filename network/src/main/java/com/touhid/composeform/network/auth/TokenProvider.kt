package com.touhid.composeform.network.auth

/**
 * Storage for the auth token is a consuming module's concern (same deferral pattern as
 * `@BaseUrl`) - `:network` only depends on this contract to attach `Authorization` headers.
 */
interface TokenProvider {
    fun getToken(): String?
    fun setToken(token: String?)
}
