package com.touhid.composeform.network

import com.touhid.composeform.model.AppError
import com.touhid.composeform.model.AppResult
import com.touhid.composeform.network.interceptor.ApiException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

// The safe-call entry point for repositories in feature/core-slice data modules. Runs the call on
// [dispatcher] and turns every failure into an AppError, so retrofit2/okhttp3/java.io exception
// types never leave :core:network.
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> T,
): AppResult<T> = withContext(dispatcher) {
    try {
        AppResult.Success(apiCall())
    } catch (e: CancellationException) {
        // Deliberately rethrown, not turned into a Failure, and it does NOT crash the app.
        // A CancellationException is how a coroutine stops (e.g. viewModelScope is cancelled when
        // the user leaves the screen), not an error: the coroutine library expects it to travel up
        // to the cancelled coroutine, which then ends quietly - it never reaches a
        // CoroutineExceptionHandler or Android's crash handler, and nothing is shown to the user.
        // Returning Failure instead would keep the cancelled coroutine running (updating a screen
        // that's gone, making more calls) and break structured concurrency. Must stay ahead of
        // `catch (e: Exception)` below, which would otherwise swallow it.
        // One thing to remember: withTimeout signals a timeout with TimeoutCancellationException (a
        // subclass), so it also ends the coroutine silently here; use withTimeoutOrNull to show it.
        throw e
    } catch (e: ApiException) {
        // Caught ahead of the generic IOException branch below - ApiException is an IOException
        // subtype (see ErrorInterceptor.kt), so without this branch first it would fall through to
        // NoConnection, which is wrong: this is an HTTP failure, not a connectivity one. Carries the
        // backend's own message/status instead of the generic HTTP reason phrase.
        AppResult.Failure(httpError(code = e.code, status = e.status, message = e.apiMessage))
    } catch (e: HttpException) {
        AppResult.Failure(httpError(code = e.code(), status = null, message = e.message()))
    } catch (e: SocketTimeoutException) {
        AppResult.Failure(AppError.Timeout())
    } catch (e: IOException) {
        AppResult.Failure(AppError.NoConnection())
    } catch (e: Exception) {
        AppResult.Failure(AppError.Unknown(e.message ?: "Unknown error", e))
    }
}

// A 401 that reaches here survived TokenAuthenticator's refresh attempt: the session is gone.
private fun httpError(code: Int, status: String?, message: String): AppError =
    if (code == 401) {
        AppError.Unauthorized(message)
    } else {
        AppError.Server(code = code, status = status, message = message)
    }
