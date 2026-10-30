package com.touhid.composeform.database

import com.touhid.composeform.model.AppError
import com.touhid.composeform.model.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

// The database counterpart of :core:network's safeApiCall: runs a DAO write (or one-shot read) and
// returns AppResult, so a repository mixing API and database calls hands its ViewModel one result
// type. Any failure (SQLiteException and friends) becomes AppError.Unknown with the exception kept
// as cause; Room/SQLite exception types never reach domain or ui. CancellationException is rethrown.
// Observing reads (Flow from a DAO) don't need this: collect them directly.
suspend fun <T> safeDbCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    block: suspend () -> T,
): AppResult<T> = try {
    AppResult.Success(withContext(dispatcher) { block() })
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    AppResult.Failure(AppError.Unknown(message = "Couldn't access saved data", cause = e))
}
