package com.touhid.composeform.feature.acquisition.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// The acquisition endpoints on the main backend (@BaseUrl). Created from :core:network's main
// Retrofit in AcquisitionDataModule, so it shares that client's auth, token refresh, error mapping
// and debug mocks.
internal interface AcquisitionApi {

    @GET("v1/acquisitions")
    suspend fun getAcquisitionList(
        @Query("search") search: String?,
        @Query("page_no") pageNo: Int,
    ): AcquisitionListResponseDto

    @GET("v1/acquisitions/{leadId}")
    suspend fun getAcquisitionDetail(@Path("leadId") leadId: String): AcquisitionDetailResponseDto

    @GET("v1/acquisitions/{leadId}/reasons")
    suspend fun getAcquisitionReasons(
        @Path("leadId") leadId: String,
        @Query("type") type: String,
    ): AcquisitionReasonsResponseDto

    @POST("v1/acquisitions/{leadId}/decision")
    suspend fun submitAcquisitionDecision(
        @Path("leadId") leadId: String,
        @Body request: AcquisitionDecisionRequestDto,
    ): AcquisitionDecisionResponseDto
}
