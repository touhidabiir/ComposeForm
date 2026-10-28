package com.touhid.composeform.feature.lead.data

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// The lead endpoints on the main backend (@BaseUrl). Created from :core:network's main Retrofit in
// LeadDataModule, so it shares that client's auth, token refresh, error mapping and debug mocks.
internal interface LeadApi {

    @GET("v1/leads")
    suspend fun getLeads(
        @Query("status") status: String?,
        @Query("search") search: String?,
        @Query("page_no") pageNo: Int,
    ): LeadDashboardResponseDto

    @POST("v1/leads/{leadId}/ekyc")
    suspend fun submitEkyc(@Path("leadId") leadId: Long): EkycSubmitResponseDto
}
