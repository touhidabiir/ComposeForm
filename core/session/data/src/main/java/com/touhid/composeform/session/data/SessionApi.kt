package com.touhid.composeform.session.data

import retrofit2.http.Body
import retrofit2.http.POST

// The sign-in endpoint on the main backend (@BaseUrl). The token refresh call is not here: it
// lives in :core:network (RefreshTokenApiService), since TokenAuthenticator runs it.
internal interface SessionApi {

    @POST("v1/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto
}
