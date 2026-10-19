package com.touhid.composeform.network

sealed class NetworkError(open val message: String) {
    // status carries the app backend's own status string from its error body, when ErrorInterceptor
    // (network/.../interceptor/ErrorInterceptor.kt) parsed one on a non-2xx response - null for a
    // plain HttpException that didn't go through it (the other 3 base URLs, or a body it couldn't
    // parse), distinct from code (the transport-level HTTP status).
    data class Http(val code: Int, val errorBody: String?, val status: String? = null, override val message: String) : NetworkError(message)
    data class Timeout(override val message: String = "The request timed out") : NetworkError(message)
    data class NoConnection(override val message: String = "No internet connection") : NetworkError(message)
    data class Unexpected(override val message: String, val cause: Throwable? = null) : NetworkError(message)
}
