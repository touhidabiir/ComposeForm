package com.touhid.composeform.network.api

import com.touhid.composeform.network.model.RefreshTokenRequest
import com.touhid.composeform.network.model.RefreshTokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// Kept separate from AppApiService (and served by its own Retrofit, see NetworkModule) since it's
// only ever called from TokenAuthenticator - which runs synchronously inside OkHttp's call chain,
// hence a blocking Call<T> instead of a suspend function.
internal interface RefreshTokenApiService {

    @POST("v1/auth/refresh")
    fun refresh(@Body request: RefreshTokenRequest): Call<RefreshTokenResponse>
}
