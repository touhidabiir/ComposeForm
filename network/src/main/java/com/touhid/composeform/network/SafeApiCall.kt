package com.touhid.composeform.network

import com.touhid.composeform.network.interceptor.ApiException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> T,
): NetworkResult<T> = withContext(dispatcher) {
    try {
        NetworkResult.Success(apiCall())
    } catch (e: CancellationException) {
        throw e
    } catch (e: ApiException) {
        // Caught ahead of the generic IOException branch below - ApiException is an IOException
        // subtype (see ErrorInterceptor.kt), so without this specific branch first it would fall
        // through to NoConnection, which is wrong: this is a backend-reported business error, not
        // a connectivity failure.
        NetworkResult.Failure(NetworkError.Api(status = e.status, message = e.apiMessage))
    } catch (e: HttpException) {
        NetworkResult.Failure(
            NetworkError.Http(
                code = e.code(),
                errorBody = e.response()?.errorBody()?.string(),
                message = e.message(),
            )
        )
    } catch (e: SocketTimeoutException) {
        NetworkResult.Failure(NetworkError.Timeout())
    } catch (e: IOException) {
        NetworkResult.Failure(NetworkError.NoConnection())
    } catch (e: Exception) {
        NetworkResult.Failure(NetworkError.Unexpected(e.message ?: "Unknown error", e))
    }
}
