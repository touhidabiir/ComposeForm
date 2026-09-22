package com.touhid.composeform.network

sealed class NetworkError(open val message: String) {
    data class Http(val code: Int, val errorBody: String?, override val message: String) : NetworkError(message)
    // Thrown by ErrorInterceptor (network/.../interceptor/ErrorInterceptor.kt) when the app's own
    // backend reports a business-logic failure via its {is_error, message, status} body envelope,
    // rather than (or alongside) a non-2xx HTTP status - status carries the backend's own status
    // string as-is, distinct from Http.code (the transport-level HTTP status).
    data class Api(val status: String, override val message: String) : NetworkError(message)
    data class Timeout(override val message: String = "The request timed out") : NetworkError(message)
    data class NoConnection(override val message: String = "No internet connection") : NetworkError(message)
    data class Unexpected(override val message: String, val cause: Throwable? = null) : NetworkError(message)
}
