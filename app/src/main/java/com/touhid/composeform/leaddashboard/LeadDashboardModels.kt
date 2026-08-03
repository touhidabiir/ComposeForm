package com.touhid.composeform.leaddashboard

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

// The dashboard's filter tabs - "e-KYC" isn't a raw LeadStatus, it's approved leads that already
// have an ekycSubmitter, so this stays a UI-only concept distinct from the wire model below.
enum class LeadStatusFilter(val label: String) {
    Approved("অনুমোদিত"),
    Rejected("বাতিল"),
    Pending("পেন্ডিং"),
    EKyc("ই-কেওয়াইসি"),
}

// Mirrors the backend's lead list response shape 1:1 - field names/nesting match the API's JSON
// exactly (via @SerializedName for its snake_case keys), Gson-reflected the same way :network's
// own request/response models are (see CLAUDE.md's Network boundary section) rather than
// kotlinx.serialization's fail-fast @Serializable approach.
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

data class LeadCloser(
    val name: String,
    @SerializedName("employee_id") val employeeId: String,
    @SerializedName("whitelisting_number") val whitelistingNumber: String,
    @SerializedName("serving_ma") val servingMa: String,
)

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

private val gson = Gson()

fun parseLeadDashboardResponse(jsonString: String): LeadDashboardResponse =
    gson.fromJson(jsonString, LeadDashboardResponse::class.java)

// Stands in for a real backend call, same spirit as DemoFormApi - a JSON string round-tripped
// through the real response model rather than Kotlin objects built by hand.
fun sampleLeadDashboardResults(): List<LeadListItem> = parseLeadDashboardResponse(SAMPLE_LEAD_DASHBOARD_JSON).data.results

private val SAMPLE_LEAD_DASHBOARD_JSON = """
{
  "data": {
    "count": 150,
    "page_no": 1,
    "page_size": 20,
    "total_pages": 8,
    "results": [
      {
        "id": 100238471,
        "display_id": "LEAD-2026-100238471",
        "shop_name": "Romij Electric",
        "wallet_number": "01723456789",
        "address": "2 No. Road, Block-B, Syed Shah Road, Bakalia",
        "status": "pending",
        "premiumness_score": 61.2,
        "can_submit_ekyc": false,
        "lead_closer": {
          "name": "Jamal Bhuiyan",
          "employee_id": "A11002912",
          "whitelisting_number": "01930119876",
          "serving_ma": "01930198765"
        },
        "reviewer": null,
        "ekyc_submitter": null,
        "rejection": null,
        "created_at": "2026-07-15T10:30:00+06:00"
      },
      {
        "id": 100238472,
        "display_id": "LEAD-2026-100238472",
        "shop_name": "Test Merchant A",
        "wallet_number": "01208567890",
        "address": "2 No. Road, Block-B, Syed Shah Road, Bakalia",
        "status": "approved",
        "premiumness_score": 72.4,
        "can_submit_ekyc": true,
        "lead_closer": {
          "name": "Jamal Bhuiyan",
          "employee_id": "A11002912",
          "whitelisting_number": "01930119876",
          "serving_ma": "01930198765"
        },
        "reviewer": {
          "name": "Khastogir Alom",
          "designation": "OM",
          "territory": "Bakalia"
        },
        "ekyc_submitter": null,
        "rejection": null,
        "created_at": "2026-07-14T09:05:00+06:00"
      },
      {
        "id": 100238473,
        "display_id": "LEAD-2026-100238473",
        "shop_name": "Test Merchant A",
        "wallet_number": "01208567890",
        "address": "2 No. Road, Block-B, Syed Shah Road, Bakalia",
        "status": "approved",
        "premiumness_score": 84.9,
        "can_submit_ekyc": false,
        "lead_closer": {
          "name": "Jamal Bhuiyan",
          "employee_id": "A11002912",
          "whitelisting_number": "01930119876",
          "serving_ma": "01930198765"
        },
        "reviewer": {
          "name": "Khastogir Alom",
          "designation": "OM",
          "territory": "Bakalia"
        },
        "ekyc_submitter": {
          "name": "Jamal Bhuiyan"
        },
        "rejection": null,
        "created_at": "2026-07-10T14:45:00+06:00"
      },
      {
        "id": 100238474,
        "display_id": "LEAD-2026-100238474",
        "shop_name": "Test Merchant A",
        "wallet_number": "01208567890",
        "address": "2 No. Road, Block-B, Syed Shah Road, Bakalia",
        "status": "rejected",
        "premiumness_score": 38.0,
        "can_submit_ekyc": false,
        "lead_closer": {
          "name": "Rahgir Bhuiyan",
          "employee_id": "A11002913",
          "whitelisting_number": "12930198921",
          "serving_ma": "01930198766"
        },
        "reviewer": {
          "name": "Rahgir Alom",
          "designation": "TM",
          "territory": "Dhanmondi Outer"
        },
        "ekyc_submitter": null,
        "rejection": {
          "reason": "এই লিডটি অননুমোদিত লোকেশনের জন্য ইতিমধ্যে বাতিল করা হয়েছে।"
        },
        "created_at": "2026-07-01T11:00:00+06:00"
      }
    ]
  }
}
""".trimIndent()
