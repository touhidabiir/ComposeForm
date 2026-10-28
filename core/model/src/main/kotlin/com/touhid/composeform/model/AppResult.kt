package com.touhid.composeform.model

// What every repository returns. Lives in pure-Kotlin :core:model (not :core:network) so domain
// modules can declare repository interfaces with it without seeing any networking type; the
// network-specific NetworkResult/NetworkError are mapped into this once, in :core:network's
// safeAppCall.
sealed interface AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>
}

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Failure -> this
}
