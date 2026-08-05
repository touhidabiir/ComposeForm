package com.touhid.composeform.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

// :network's base URL is still a dummy placeholder (see CLAUDE.md's Network boundary notes) - no
// real backend exists yet for the lead/acquisition endpoints. Until one does, short-circuit those
// requests here with the same canned JSON every time, ignoring query params (status/search/
// page_no) and path args (leadId) entirely, so the full Retrofit/OkHttp/safeApiCall pipeline still
// runs for real - only the transport is faked.
// TODO: delete this whole file (and its wiring in NetworkModule.provideOkHttpClient) once a real
// backend is live - see MockJson.kt too, it only exists to back this class.
internal class MockDataInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val json = when {
            path.endsWith("/v1/leads") -> MockJson.LEAD_DASHBOARD
            path.contains("/v1/acquisitions/") -> MockJson.ACQUISITION_DETAIL
            path.endsWith("/v1/acquisitions") -> MockJson.ACQUISITION_LIST
            else -> null
        } ?: return chain.proceed(request)

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(json.toResponseBody("application/json".toMediaType()))
            .build()
    }
}
