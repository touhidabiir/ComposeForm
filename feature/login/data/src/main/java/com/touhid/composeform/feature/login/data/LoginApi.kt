package com.touhid.composeform.feature.login.data

import retrofit2.http.Body
import retrofit2.http.POST

// The sign-in endpoints on the main backend (@BaseUrl). The token refresh call is not here: it
// lives in :core:network (RefreshTokenApiService), since TokenAuthenticator runs it.
internal interface LoginApi {

    // Sends the one-time password by SMS. Only the HTTP status matters; the body is ignored.
    @POST("v1/auth/otp/request")
    suspend fun requestOtp(@Body request: OtpRequestDto)

    @POST("v1/auth/otp/verify")
    suspend fun verifyOtp(@Body request: OtpVerifyRequestDto): LoginResponseDto
}
