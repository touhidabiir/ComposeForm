package com.touhid.composeform.network.interceptor

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

// Generous but bounded - large enough for any realistic app-API error body, while still refusing
// to buffer an unbounded/runaway response fully into memory just to read its message/status.
private const val ErrorPeekByteLimit = 1024 * 1024L

private val gson = Gson()

private fun JsonObject.stringOrNull(name: String): String? = get(name)?.takeIf { it.isJsonPrimitive }?.asString

// The app's own backend's error body shape on a non-2xx response - {message, status}. Read
// defensively: a body that isn't valid JSON, or doesn't have these fields, just falls back to the
// HTTP reason phrase below rather than failing the request a second way.
private fun parseErrorBody(bodyString: String): Pair<String?, String?> {
    val json = runCatching { gson.fromJson(bodyString, JsonObject::class.java) }.getOrNull() ?: return null to null
    return json.stringOrNull("message") to json.stringOrNull("status")
}

// The app's API is treated as erroring purely on HTTP status (response.isSuccessful), not by
// reading a body field like is_error - that isn't a reliable discriminator here. What this
// interceptor does is enrich the failure: on a non-2xx response, it reads the body's own
// {message, status} (when present) and throws ApiException carrying them, instead of the generic
// reason-phrase message a bare HttpException (SafeApiCall.kt) would otherwise carry. peekBody
// reads a copy of the body without consuming it, so the real Retrofit converter still reads the
// original, untouched body afterwards.
internal class ErrorInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.isSuccessful) return response

        val bodyString = response.body?.let { runCatching { response.peekBody(ErrorPeekByteLimit).string() }.getOrNull() }
        val (message, status) = bodyString?.let(::parseErrorBody) ?: (null to null)

        throw ApiException(
            code = response.code,
            status = status,
            rawBody = bodyString,
            apiMessage = message ?: response.message,
        )
    }
}

// Thrown by ErrorInterceptor, caught specifically by safeApiCall (SafeApiCall.kt) before its
// generic IOException branch - an IOException subtype, not a plain RuntimeException, since OkHttp
// interceptors are only meant to throw IOException (other interceptors and OkHttp's own retry
// logic assume that contract). Maps onto the same NetworkError.Http case a plain HttpException
// would (this represents the same category of failure, just with a better message) rather than
// its own separate NetworkError case.
internal class ApiException(val code: Int, val status: String?, val rawBody: String?, val apiMessage: String) : IOException(apiMessage)
