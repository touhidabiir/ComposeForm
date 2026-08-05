package com.touhid.composeform.network.model

import com.google.gson.annotations.SerializedName

// Mirrors the backend's lead list response shape 1:1 - field names/nesting match the API's JSON
// exactly (via @SerializedName for its snake_case keys), Gson-reflected the same way :network's
// other request/response models are (see CLAUDE.md's Network boundary section).
data class LeadDashboardResponse(
    val data: LeadDashboardPage,
)

data class LeadDashboardPage(
    val count: Int,
    @SerializedName("page_no") val pageNo: Int,
    @SerializedName("page_size") val pageSize: Int,
    @SerializedName("total_pages") val totalPages: Int,
    val results: List<LeadListItem>,
)

enum class LeadStatus {
    @SerializedName("pending") Pending,
    @SerializedName("approved") Approved,
    @SerializedName("rejected") Rejected,
}

data class Reviewer(
    val name: String,
    val designation: String,
    val territory: String,
)

data class EkycSubmitter(
    val name: String,
)

data class Rejection(
    val reason: String,
)

data class LeadListItem(
    val id: Long,
    @SerializedName("display_id") val displayId: String,
    @SerializedName("shop_name") val shopName: String,
    @SerializedName("wallet_number") val walletNumber: String,
    val address: String,
    val status: LeadStatus,
    @SerializedName("premiumness_score") val premiumnessScore: Double,
    @SerializedName("can_submit_ekyc") val canSubmitEkyc: Boolean,
    @SerializedName("lead_closer") val leadCloser: LeadCloser,
    val reviewer: Reviewer? = null,
    @SerializedName("ekyc_submitter") val ekycSubmitter: EkycSubmitter? = null,
    val rejection: Rejection? = null,
    @SerializedName("created_at") val createdAt: String,
)
