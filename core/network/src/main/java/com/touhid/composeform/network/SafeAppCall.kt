package com.touhid.composeform.network

import com.touhid.composeform.model.AppError
import com.touhid.composeform.model.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// The AppResult-returning twin of safeApiCall, for repositories in feature/core-slice data modules.
// Same exception handling (it delegates to safeApiCall), then the one NetworkError -> AppError
// mapping, so no data module ever sees NetworkResult/NetworkError. safeApiCall stays for :data's
// AppRepository until its remaining endpoints move into slices.
suspend fun <T> safeAppCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> T,
): AppResult<T> = when (val result = safeApiCall(dispatcher, apiCall)) {
    is NetworkResult.Success -> AppResult.Success(result.data)
    is NetworkResult.Failure -> AppResult.Failure(result.error.toAppError())
}

internal fun NetworkError.toAppError(): AppError = when (this) {
    is NetworkError.Http -> if (code == 401) {
        AppError.Unauthorized(message)
    } else {
        AppError.Server(code = code, status = status, message = message)
    }
    is NetworkError.Timeout -> AppError.Timeout(message)
    is NetworkError.NoConnection -> AppError.NoConnection(message)
    is NetworkError.Unexpected -> AppError.Unknown(message, cause)
}
