package com.touhid.composeform.network.api

import com.touhid.composeform.network.model.AcquisitionDecisionRequest
import com.touhid.composeform.network.model.AcquisitionDecisionResponse
import com.touhid.composeform.network.model.AcquisitionDetailResponse
import com.touhid.composeform.network.model.AcquisitionListResponse
import com.touhid.composeform.network.model.AcquisitionReasonsResponse
import com.touhid.composeform.network.model.AdminDetails
import com.touhid.composeform.network.model.AdminSummary
import com.touhid.composeform.network.model.LeadDashboardResponse
import com.touhid.composeform.network.model.LoginRequest
import com.touhid.composeform.network.model.LoginResponse
import com.touhid.composeform.network.model.ManagerSummary
import com.touhid.composeform.network.model.SpecificFormResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface AppApiService {

    @POST("v1/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("v1/manager/list")
    suspend fun getManagerList(): List<ManagerSummary>

    @GET("v1/admin/list")
    suspend fun getAdminList(): List<AdminSummary>

    @GET("v1/admin/details/{id}")
    suspend fun getAdminDetails(@Path("id") id: String): AdminDetails

    @GET("v1/forms/specific")
    suspend fun getSpecificForm(): SpecificFormResponse

    @GET("v1/leads")
    suspend fun getLeadDashboard(
        @Query("status") status: String?,
        @Query("search") search: String?,
        @Query("page_no") pageNo: Int,
    ): LeadDashboardResponse

    @GET("v1/acquisitions")
    suspend fun getAcquisitionList(
        @Query("search") search: String?,
        @Query("page_no") pageNo: Int,
    ): AcquisitionListResponse

    @GET("v1/acquisitions/{leadId}")
    suspend fun getAcquisitionDetail(@Path("leadId") leadId: String): AcquisitionDetailResponse

    @GET("v1/acquisitions/{leadId}/reasons")
    suspend fun getAcquisitionReasons(
        @Path("leadId") leadId: String,
        @Query("type") type: String,
    ): AcquisitionReasonsResponse

    @POST("v1/acquisitions/{leadId}/decision")
    suspend fun submitAcquisitionDecision(
        @Path("leadId") leadId: String,
        @Body request: AcquisitionDecisionRequest,
    ): AcquisitionDecisionResponse
}
