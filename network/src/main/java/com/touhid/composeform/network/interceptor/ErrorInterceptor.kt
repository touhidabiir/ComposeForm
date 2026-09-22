package com.touhid.composeform.network.interceptor

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

// Generous but bounded - large enough for any realistic app-API body (paginated lists are capped
// by page_size), while still refusing to buffer an unbounded/runaway response fully into memory
// just to inspect its is_error field.
private const val ErrorPeekByteLimit = 1024 * 1024L

private val gson = Gson()

// The app's own backend's generic envelope - every response (success and failure alike) carries
// is_error/message at the top level alongside the endpoint's own "data" (see
// AcquisitionDetailResponse, AcquisitionReasonsResponse, AcquisitionDecisionResponse in
// network/.../model/) - this isn't a separate error-only body shape, just the same envelope with
// is_error true/1. status is this app's addition on top of that (not present in today's mock
// payloads - MockJson.kt has none), read defensively as optional so its absence never breaks
// parsing.
//
// is_error's own JSON type is inconsistent across endpoints - some send a JSON boolean
// (true/false), others a JSON number (0/1) - already discovered and documented in
// AcquisitionModels.kt's AcquisitionReasonsResponse. A single Gson-typed field (Int or Boolean)
// would hard-fail to parse whichever shape it wasn't declared for, so this reads the raw
// JsonObject instead of deserializing into a fixed-type data class, and interprets is_error
// leniently regardless of which JSON token it arrives as.
private fun JsonObject.isErrorFlag(): Boolean {
    val element = get("is_error") ?: return false
    if (!element.isJsonPrimitive) return false
    val primitive = element.asJsonPrimitive
    return when {
        primitive.isBoolean -> primitive.asBoolean
        primitive.isNumber -> primitive.asInt != 0
        else -> false
    }
}

private fun JsonObject.stringOrEmpty(name: String): String = get(name)?.takeIf { it.isJsonPrimitive }?.asString.orEmpty()

// The app's API reports business-logic failures via is_error in the response body - sometimes
// alongside a non-2xx HTTP status, sometimes on a plain 200 - rather than signaling failure
// through the HTTP status code alone the way HttpException (SafeApiCall.kt) already assumes.
// peekBody reads a copy of the body without consuming it, so the real Retrofit converter still
// reads the original, untouched body for every response (is_error false/0 included). Throwing
// ApiException here, before returning control to Retrofit, means safeApiCall sees the backend's
// own message instead of a generic "HTTP 400" one.
internal class ErrorInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.body == null) return response

        val bodyString = runCatching { response.peekBody(ErrorPeekByteLimit).string() }.getOrNull() ?: return response
        val json = runCatching { gson.fromJson(bodyString, JsonObject::class.java) }.getOrNull() ?: return response
        if (!json.isErrorFlag()) return response

        throw ApiException(status = json.stringOrEmpty("status"), apiMessage = json.stringOrEmpty("message"))
    }
}

// Thrown by ErrorInterceptor, caught specifically by safeApiCall (SafeApiCall.kt) before its
// generic IOException branch - an IOException subtype, not a plain RuntimeException, since OkHttp
// interceptors are only meant to throw IOException (other interceptors and OkHttp's own retry
// logic assume that contract).
internal class ApiException(val status: String, val apiMessage: String) : IOException(apiMessage)
