package com.touhid.composeform.network.mock

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

// The base URLs are still dummy placeholders (see CLAUDE.md's Network boundary notes) - no
// real backend exists yet for the lead/acquisition endpoints. Until one does, short-circuit those
// requests here with the same canned JSON every time, ignoring query params (status/search/
// page_no) and path args (leadId) entirely - except the reasons endpoint below, which is the one
// case that needs its "type" query param inspected to pick the right canned list - so the full
// Retrofit/OkHttp/safeApiCall pipeline still runs for real, only the transport is faked.
// TODO: delete this whole module (and the @MockInterceptor/@MockEnabled wiring in :core:network
// and :app) once a real backend is live.
internal class MockDataInterceptor : Interceptor {

    private companion object {
        val PhoneNumberField = Regex("\"phoneNumber\"\\s*:\\s*\"(\\d+)\"")
    }

    private fun Request.bodyText(): String {
        val buffer = Buffer()
        body?.writeTo(buffer)
        return buffer.readUtf8()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val json = when {
            // Sign-in: any phone number gets an OTP, and any OTP is accepted. The user type
            // follows the number's last digit: odd signs in an approver, even an agent.
            path.endsWith("/v1/auth/otp/request") -> MockJson.OTP_REQUEST_SUCCESS
            path.endsWith("/v1/auth/otp/verify") -> {
                val lastDigit = request.bodyText().let(PhoneNumberField::find)?.groupValues?.get(1)?.lastOrNull()
                MockJson.otpVerifySuccess(role = if (lastDigit != null && lastDigit.digitToInt() % 2 == 1) "approver" else "agent")
            }
            path.endsWith("/v1/leads") -> MockJson.LEAD_DASHBOARD
            path.endsWith("/ekyc") -> MockJson.EKYC_SUBMIT_SUCCESS
            path.endsWith("/v1/forms/specific") -> MockJson.SPECIFIC_FORM
            path.endsWith("/v1/forms/specific/submit") -> MockJson.SPECIFIC_FORM_SUBMIT_SUCCESS
            // Checked before the "/v1/acquisitions/" contains-branch below, since
            // ".../acquisitions/{leadId}/reasons" and ".../decision" would otherwise match that
            // broader branch first and incorrectly return the acquisition-detail JSON.
            path.endsWith("/reasons") -> {
                if (request.url.queryParameter("type") == "reject") {
                    MockJson.ACQUISITION_REASONS_REJECT
                } else {
                    MockJson.ACQUISITION_REASONS_ACCEPT
                }
            }
            path.endsWith("/decision") -> MockJson.ACQUISITION_DECISION_SUCCESS
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
