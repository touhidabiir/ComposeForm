package com.touhid.composeform.network.model

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
    @SerializedName("premiumness_score_ranges") val premiumnessScoreRanges: List<PremiumnessScoreRange>,
    val images: AcquisitionImages,
    @SerializedName("outlet_info") val outletInfo: OutletInfo,
    @SerializedName("digital_payment") val digitalPayment: DigitalPayment,
    @SerializedName("contact_info") val contactInfo: ContactInfo,
    @SerializedName("wallet_info") val walletInfo: WalletInfo,
    @SerializedName("survey_responses") val surveyResponses: List<SurveyResponse>,
    val audit: AcquisitionAudit,
)

// One tier of the premiumness score gauge - min/max score, whether this detail's score falls in
// it, and the color its bar segment should render in. The backend drives all of this now (bounds,
// which tier is active, and the color), rather than a client-side hardcoded band table.
data class PremiumnessScoreRange(
    @SerializedName("min_score") val minScore: Int,
    @SerializedName("max_score") val maxScore: Int,
    @SerializedName("is_active") val isActive: Boolean,
    val color: String,
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
