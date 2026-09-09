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

// The pending/approved/rejected <-> JSON string mapping lives entirely in LeadStatusTypeAdapter
// (registered on the Gson instance in NetworkModule), not via @SerializedName here - Gson's
// default enum handling silently maps an unrecognized value to null, which would then sit inside
// this non-null Kotlin property as an unchecked platform null. The adapter maps anything it
// doesn't recognize to Unknown instead, so a new backend status is visible/handleable rather than
// silently breaking Kotlin's null-safety.
enum class LeadStatus {
    Pending,
    Approved,
    Rejected,
    // Fallback for any status value the backend sends that this app doesn't know about yet -
    // never itself present in the wire format. Existing LeadStatus checks compare against a
    // specific known constant inside a when {} with an else/no-match fallthrough, so Unknown
    // already falls through those safely - add an explicit branch wherever that's not enough.
    Unknown,
}

data class Reviewer(
    val name: String,
    val designation: String,
    @SerializedName("serving_ma") val servingMa: String,
    @SerializedName("hierarchy_key") val hierarchyKey: String,
    @SerializedName("hierarchy_value") val hierarchyValue: String,
)

data class EkycSubmitter(
    val name: String,
)

data class Rejection(
    val reasons: List<RejectionReason>,
    val note: String,
    @SerializedName("reviewed_at") val reviewedAt: String,
)

data class RejectionReason(
    val id: Int,
    val reason: String,
)

data class EkycSubmitResponse(
    @SerializedName("is_error") val isError: Boolean,
    val message: String,
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
