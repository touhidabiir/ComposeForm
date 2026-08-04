package com.touhid.composeform.acquisition

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class AcquisitionPerson(val name: String, val idOrPhone: String)

// Mirrors the backend's acquisition list response shape 1:1 - field names/nesting match the
// API's JSON exactly (via @SerializedName for its snake_case keys), Gson-reflected the same way
// LeadDashboardModels.kt is.
data class AcquisitionListResponse(
    val data: AcquisitionListPage,
)

data class AcquisitionListPage(
    val count: Int,
    @SerializedName("page_no") val pageNo: Int,
    @SerializedName("page_size") val pageSize: Int,
    @SerializedName("total_pages") val totalPages: Int,
    val results: List<AcquisitionListItem>,
)

data class LeadCloser(
    val name: String,
    @SerializedName("employee_id") val employeeId: String,
    @SerializedName("whitelisting_number") val whitelistingNumber: String,
    @SerializedName("serving_ma") val servingMa: String,
)

data class AcquisitionListItem(
    val id: Long,
    @SerializedName("display_id") val displayId: String,
    @SerializedName("shop_name") val shopName: String,
    @SerializedName("wallet_number") val walletNumber: String,
    val address: String,
    @SerializedName("lead_closer") val leadCloser: LeadCloser,
    @SerializedName("submitted_at") val submittedAt: String,
    @SerializedName("can_review") val canReview: Boolean,
)

private val gson = Gson()

fun parseAcquisitionListResponse(jsonString: String): AcquisitionListResponse =
    gson.fromJson(jsonString, AcquisitionListResponse::class.java)

// Stands in for a real backend call, same spirit as DemoFormApi/sampleLeadDashboardResults - a
// JSON string round-tripped through the real response model rather than Kotlin objects built by hand.
fun sampleAcquisitionListItems(): List<AcquisitionListItem> =
    parseAcquisitionListResponse(SAMPLE_ACQUISITION_LIST_JSON).data.results

private val SAMPLE_ACQUISITION_LIST_JSON = """
{
  "data": {
    "count": 45,
    "page_no": 1,
    "page_size": 20,
    "total_pages": 3,
    "results": [
      {
        "id": 100238471,
        "display_id": "LEAD-2026-100238471",
        "shop_name": "Romij Electric",
        "wallet_number": "01723456789",
        "address": "2 No. Road, Block-B, Syed Shah Road, Bakalia",
        "lead_closer": {
          "name": "Jamal Bhuiyan",
          "employee_id": "A11002912",
          "whitelisting_number": "01930119876",
          "serving_ma": "01930198765"
        },
        "submitted_at": "2026-07-13T14:30:00+06:00",
        "can_review": true
      },
      {
        "id": 100238472,
        "display_id": "LEAD-2026-100238472",
        "shop_name": "Anowar Traders",
        "wallet_number": "01812345678",
        "address": "5 No. Road, Block-C, CDA Avenue, Bakalia",
        "lead_closer": {
          "name": "Jamal Bhuiyan",
          "employee_id": "A11002912",
          "whitelisting_number": "01930119876",
          "serving_ma": "01930198765"
        },
        "submitted_at": "2026-07-12T11:15:00+06:00",
        "can_review": true
      }
    ]
  }
}
""".trimIndent()

data class LabeledPair(val label: String, val value: String)

data class AcquisitionDetail(
    val merchantName: String,
    val phoneNumber: String,
    val score: Int,
    val maxScore: Int,
    val photoCaptions: List<String>,
    val shopOwner: AcquisitionPerson,
    val shopOperator: AcquisitionPerson,
    val outletInfo: List<LabeledPair>,
    val walletInfo: List<LabeledPair>,
)

fun sampleAcquisitionDetail(): AcquisitionDetail = AcquisitionDetail(
    merchantName = "টেস্ট মার্চেন্ট এ",
    phoneNumber = "01113-783737",
    score = 89,
    maxScore = 100,
    photoCaptions = listOf(
        "আউটলেটের বাহিরের ছবি",
        "আউটলেটের ভিতরের ছবি",
        "ব্যবসার পরিচয়পত্রের ছবি",
    ),
    shopOwner = AcquisitionPerson("Raja Ahmed Shetu", "01723-456-789"),
    shopOperator = AcquisitionPerson("Kalam Bashir", "01723-456-789"),
    outletInfo = listOf(
        LabeledPair("BWCC", "5002"),
        LabeledPair("Product Type", "Merchant Plus Lite B"),
        LabeledPair("Outlet location type", "বহুতল ভবন"),
        LabeledPair("Outlet ambiance", "খুব ভালো"),
        LabeledPair("Card payment available", "Yes"),
        LabeledPair("Other MFS payment available", "No"),
    ),
    walletInfo = listOf(
        LabeledPair("Proposed Wallet Number", "01723-456-789"),
        LabeledPair("SIM shops outlet?", "হ্যাঁ"),
        LabeledPair("SIM used in card/mobile?", "হ্যাঁ"),
        LabeledPair("SIM owned by Shop Owner?", "হ্যাঁ"),
    ),
)
