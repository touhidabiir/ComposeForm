package com.touhid.composeform.acquisition

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

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

// Mirrors the backend's acquisition detail response shape 1:1, same Gson-reflected pattern as
// the list models above - "data" nests everything, "submitted_by" reuses the same LeadCloser
// shape the list screen's "lead_closer" already models.
data class AcquisitionDetailResponse(
    @SerializedName("is_error") val isError: Boolean,
    val message: String,
    val data: AcquisitionDetail,
)

data class AcquisitionDetail(
    val id: Long,
    @SerializedName("display_id") val displayId: String,
    @SerializedName("shop_name") val shopName: String,
    @SerializedName("wallet_number") val walletNumber: String,
    val status: String,
    @SerializedName("premiumness_score") val premiumnessScore: Double,
    @SerializedName("premiumness_band") val premiumnessBand: String,
    val images: AcquisitionImages,
    @SerializedName("outlet_info") val outletInfo: OutletInfo,
    @SerializedName("digital_payment") val digitalPayment: DigitalPayment,
    @SerializedName("contact_info") val contactInfo: ContactInfo,
    @SerializedName("wallet_info") val walletInfo: WalletInfo,
    @SerializedName("survey_responses") val surveyResponses: List<SurveyResponse>,
    val audit: AcquisitionAudit,
)

data class AcquisitionImages(
    @SerializedName("shop_image_outside") val shopImageOutside: String,
    @SerializedName("shop_image_inside") val shopImageInside: String,
    @SerializedName("business_proof_image") val businessProofImage: String,
)

data class OutletInfo(
    val address: String,
    val district: String,
    val thana: String,
    @SerializedName("market_name") val marketName: String,
    @SerializedName("bmcc_code") val bmccCode: String,
    @SerializedName("bmcc_name") val bmccName: String,
    @SerializedName("product_type") val productType: String,
    @SerializedName("outlet_location_type") val outletLocationType: String,
    @SerializedName("outlet_type") val outletType: String,
)

data class DigitalPayment(
    @SerializedName("card_payment_available") val cardPaymentAvailable: Boolean,
    @SerializedName("other_mfs_available") val otherMfsAvailable: Boolean,
    val facilities: List<Facility>,
)

data class Facility(val name: String, val completed: Boolean)

data class ContactInfo(
    @SerializedName("contact_person") val contactPerson: ContactPerson,
    @SerializedName("outlet_owner") val outletOwner: ContactPerson,
)

data class ContactPerson(
    val name: String,
    @SerializedName("phone_number") val phoneNumber: String,
    val designation: String? = null,
)

data class WalletInfo(
    @SerializedName("proposed_wallet_number") val proposedWalletNumber: String,
    @SerializedName("sim_stays_at_outlet") val simStaysAtOutlet: Boolean,
    @SerializedName("sim_used_in_smartphone") val simUsedInSmartphone: Boolean,
    @SerializedName("sim_owned_by_shop_owner") val simOwnedByShopOwner: Boolean,
)

data class SurveyResponse(
    val question: String,
    val answer: String,
    val points: Double?,
)

data class AcquisitionAudit(
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("submitted_at") val submittedAt: String,
    @SerializedName("submitted_by") val submittedBy: LeadCloser,
)

fun parseAcquisitionDetailResponse(jsonString: String): AcquisitionDetailResponse =
    gson.fromJson(jsonString, AcquisitionDetailResponse::class.java)

fun sampleAcquisitionDetail(): AcquisitionDetail =
    parseAcquisitionDetailResponse(SAMPLE_ACQUISITION_DETAIL_JSON).data

private val SAMPLE_ACQUISITION_DETAIL_JSON = """
{
  "is_error": false,
  "message": "success",
  "data": {
    "id": 100238471,
    "display_id": "LEAD-2026-100238471",
    "shop_name": "Romij Electric",
    "wallet_number": "01723456789",
    "status": "submitted",
    "premiumness_score": 72.4,
    "premiumness_band": "50-74",
    "images": {
      "shop_image_outside": "https://storage.example.com/shop-outside.jpg",
      "shop_image_inside": "https://storage.example.com/shop-inside.jpg",
      "business_proof_image": "https://storage.example.com/business-proof.jpg"
    },
    "outlet_info": {
      "address": "2 No. Road, Block-B, Syed Shah Road, Bakalia",
      "district": "Chattogram",
      "thana": "Bakalia",
      "market_name": "Avengers Tower",
      "bmcc_code": "5002",
      "bmcc_name": "Hardware & Electronics",
      "product_type": "Merchant Plus Lite A",
      "outlet_location_type": "Roadside",
      "outlet_type": "Semi-permanent"
    },
    "digital_payment": {
      "card_payment_available": true,
      "other_mfs_available": false,
      "facilities": [
        {"name": "Card payment", "completed": true}
      ]
    },
    "contact_info": {
      "contact_person": {
        "name": "Kalam Bashir",
        "phone_number": "01723456789",
        "designation": "Manager"
      },
      "outlet_owner": {
        "name": "Raju Ahmed Shetu",
        "phone_number": "01723456789"
      }
    },
    "wallet_info": {
      "proposed_wallet_number": "01723456789",
      "sim_stays_at_outlet": true,
      "sim_used_in_smartphone": true,
      "sim_owned_by_shop_owner": true
    },
    "survey_responses": [
      {"question": "Is the outlet inside a market?", "answer": "No", "points": 6.4},
      {"question": "Is it air-conditioned?", "answer": "Yes", "points": 0.0},
      {"question": "Entrance door type?", "answer": "Glass", "points": 12.6},
      {"question": "Illuminated signboard?", "answer": "Yes", "points": null},
      {"question": "Is the signboard clean?", "answer": "Yes", "points": 10.2},
      {"question": "Tube lights count?", "answer": "4-6", "points": 5.1},
      {"question": "Spot lights count?", "answer": "6-10", "points": 8.0},
      {"question": "Separate cash counter?", "answer": "Yes", "points": 3.2},
      {"question": "Accepts card payments?", "answer": "No", "points": 0.0},
      {"question": "Provides printed bills?", "answer": "Yes", "points": 20.7}
    ],
    "audit": {
      "created_at": "2026-07-15T10:30:00+06:00",
      "submitted_at": "2026-07-15T10:35:00+06:00",
      "submitted_by": {
        "name": "Jamal Bhuiyan",
        "employee_id": "A11002912",
        "whitelisting_number": "1930119876",
        "serving_ma": "1930198765"
      }
    }
  }
}
""".trimIndent()
