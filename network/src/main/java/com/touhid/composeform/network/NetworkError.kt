package com.touhid.composeform.network

sealed class NetworkError(open val message: String) {
    data class Http(val code: Int, val errorBody: String?, override val message: String) : NetworkError(message)
    data class Timeout(override val message: String = "The request timed out") : NetworkError(message)
    data class NoConnection(override val message: String = "No internet connection") : NetworkError(message)
    data class Unexpected(override val message: String, val cause: Throwable? = null) : NetworkError(message)
}
