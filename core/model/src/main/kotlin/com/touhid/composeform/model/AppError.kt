package com.touhid.composeform.model

// Every screen handles these the same way, whichever feature or backend they came from. message is
// always display-ready text: the backend's own error message for Server/Unauthorized when it sent
// one, otherwise a generic fallback.
sealed interface AppError {
    val message: String

    data class NoConnection(override val message: String = "No internet connection") : AppError
    data class Timeout(override val message: String = "The request timed out") : AppError

    // HTTP 401 that survived TokenAuthenticator's refresh attempt - the session is no longer valid.
    data class Unauthorized(override val message: String) : AppError

    // Any other non-2xx response. status is the backend's own status string from its error body,
    // when it sent one.
    data class Server(val code: Int, val status: String?, override val message: String) : AppError

    data class Unknown(override val message: String, val cause: Throwable? = null) : AppError
}
