package com.touhid.composeform.network.api

import com.touhid.composeform.network.auth.NoAuth
import com.touhid.composeform.network.model.PartnerStatusResponse
import retrofit2.http.GET

internal interface PartnerApiService {

    // Partner's own backend, not ours - must never receive our app's bearer token.
    @NoAuth
    @GET("v1/partner/status")
    suspend fun getStatus(): PartnerStatusResponse
}
