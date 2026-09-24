package com.touhid.composeform.network.api

import com.touhid.composeform.network.model.PartnerStatusResponse
import retrofit2.http.GET

internal interface PartnerApiService {

    @GET("v1/partner/status")
    suspend fun getStatus(): PartnerStatusResponse
}
