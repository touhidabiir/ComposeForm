package com.touhid.composeform.feature.lead.data

import com.google.gson.annotations.SerializedName
import com.touhid.composeform.network.model.LeadCloser
import com.touhid.composeform.network.model.Rejection
import com.touhid.composeform.network.model.Reviewer

// Mirror the backend's lead list response 1:1 (snake_case via @SerializedName), Gson-reflected.
// internal: nothing outside this module ever sees a DTO - LeadRepositoryImpl maps them to the
// domain models first. lead_closer/reviewer/rejection reuse :core:network's shared wire models,
// which the acquisition responses embed too.

internal data class LeadDashboardResponseDto(
    val data: LeadDashboardPageDto,
)

internal data class LeadDashboardPageDto(
    val count: Int,
    @SerializedName("page_no") val pageNo: Int,
    @SerializedName("page_size") val pageSize: Int,
    @SerializedName("total_pages") val totalPages: Int,
    val results: List<LeadListItemDto>,
)

internal enum class LeadStatusDto {
    @SerializedName("pending") Pending,
    @SerializedName("approved") Approved,
    @SerializedName("rejected") Rejected,
}

internal data class EkycSubmitterDto(
    val name: String,
)

internal data class EkycSubmitResponseDto(
    @SerializedName("is_error") val isError: Boolean,
    val message: String,
)

internal data class LeadListItemDto(
    val id: Long,
    @SerializedName("display_id") val displayId: String,
    @SerializedName("shop_name") val shopName: String,
    @SerializedName("wallet_number") val walletNumber: String,
    val address: String,
    val status: LeadStatusDto,
    @SerializedName("premiumness_score") val premiumnessScore: Double,
    @SerializedName("can_submit_ekyc") val canSubmitEkyc: Boolean,
    @SerializedName("lead_closer") val leadCloser: LeadCloser,
    val reviewer: Reviewer? = null,
    @SerializedName("ekyc_submitter") val ekycSubmitter: EkycSubmitterDto? = null,
    val rejection: Rejection? = null,
    @SerializedName("created_at") val createdAt: String,
)
