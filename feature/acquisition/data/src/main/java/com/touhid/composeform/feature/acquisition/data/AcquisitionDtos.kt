package com.touhid.composeform.feature.acquisition.data

import com.google.gson.annotations.SerializedName
import com.touhid.composeform.lead.data.LeadCloserDto
import com.touhid.composeform.lead.data.RejectionDto

// Mirror the backend's acquisition responses 1:1 (snake_case via @SerializedName), Gson-reflected.
// internal: nothing outside this module ever sees a DTO - AcquisitionRepositoryImpl maps them to
// the domain models first. lead_closer/submitted_by/rejection_reasons reuse :core:lead:data's
// shared DTOs, which the lead list response embeds too.

internal data class AcquisitionListResponseDto(
    val data: AcquisitionListPageDto,
)

internal data class AcquisitionListPageDto(
    val count: Int,
    @SerializedName("page_no") val pageNo: Int,
    @SerializedName("page_size") val pageSize: Int,
    @SerializedName("total_pages") val totalPages: Int,
    val results: List<AcquisitionListItemDto>,
)

internal data class AcquisitionListItemDto(
    val id: Long,
    @SerializedName("display_id") val displayId: String,
    @SerializedName("shop_name") val shopName: String,
    @SerializedName("wallet_number") val walletNumber: String,
    val address: String,
    @SerializedName("lead_closer") val leadCloser: LeadCloserDto,
    @SerializedName("submitted_at") val submittedAt: String,
    @SerializedName("can_review") val canReview: Boolean,
    @SerializedName("rejection_count") val rejectionCount: Int = 0,
)

internal data class AcquisitionDetailResponseDto(
    @SerializedName("is_error") val isError: Boolean,
    val message: String,
    val data: AcquisitionDetailDto,
)

internal data class AcquisitionDetailDto(
    val id: Long,
    @SerializedName("display_id") val displayId: String,
    @SerializedName("shop_name") val shopName: String,
    @SerializedName("wallet_number") val walletNumber: String,
    val status: String,
    @SerializedName("premiumness_score") val premiumnessScore: Double,
    @SerializedName("premiumness_score_ranges") val premiumnessScoreRanges: List<PremiumnessScoreRangeDto>,
    val images: AcquisitionImagesDto,
    @SerializedName("outlet_info") val outletInfo: OutletInfoDto,
    @SerializedName("digital_payment") val digitalPayment: DigitalPaymentDto,
    @SerializedName("contact_info") val contactInfo: ContactInfoDto,
    @SerializedName("wallet_info") val walletInfo: WalletInfoDto,
    @SerializedName("survey_responses") val surveyResponses: List<SurveyResponseDto>,
    val audit: AcquisitionAuditDto,
    // Nullable since a first-time submission may omit the key entirely rather than send an empty
    // list, and Gson would otherwise leave null in a non-null List field.
    @SerializedName("rejection_reasons") val rejectionReasons: List<RejectionDto>? = null,
)

internal data class PremiumnessScoreRangeDto(
    @SerializedName("min_score") val minScore: Int,
    @SerializedName("max_score") val maxScore: Int,
    @SerializedName("is_active") val isActive: Boolean,
    val color: String,
)

internal data class AcquisitionImagesDto(
    @SerializedName("shop_image_outside") val shopImageOutside: String,
    @SerializedName("shop_image_inside") val shopImageInside: String,
    @SerializedName("business_proof_image") val businessProofImage: String,
)

internal data class OutletInfoDto(
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

internal data class DigitalPaymentDto(
    @SerializedName("card_payment_available") val cardPaymentAvailable: Boolean,
    @SerializedName("other_mfs_available") val otherMfsAvailable: Boolean,
    val facilities: List<FacilityDto>,
)

internal data class FacilityDto(val name: String, val completed: Boolean)

internal data class ContactInfoDto(
    @SerializedName("contact_person") val contactPerson: ContactPersonDto,
    @SerializedName("outlet_owner") val outletOwner: ContactPersonDto,
)

internal data class ContactPersonDto(
    val name: String,
    @SerializedName("phone_number") val phoneNumber: String,
    val designation: String? = null,
)

internal data class WalletInfoDto(
    @SerializedName("proposed_wallet_number") val proposedWalletNumber: String,
    @SerializedName("sim_stays_at_outlet") val simStaysAtOutlet: Boolean,
    @SerializedName("sim_used_in_smartphone") val simUsedInSmartphone: Boolean,
    @SerializedName("sim_owned_by_shop_owner") val simOwnedByShopOwner: Boolean,
)

internal data class SurveyResponseDto(
    val question: String,
    val answer: String,
    val points: Double?,
)

internal data class AcquisitionAuditDto(
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("submitted_at") val submittedAt: String,
    @SerializedName("submitted_by") val submittedBy: LeadCloserDto,
)

// "type" echoes back which list was requested (accept/reject). This endpoint sends is_error as a
// numeric 0/1 rather than true/false, so it's typed as Int - Gson's Boolean adapter throws on a
// JSON number token, it doesn't coerce 0/1.
internal data class AcquisitionReasonsResponseDto(
    @SerializedName("is_error") val isError: Int,
    val message: String,
    val data: AcquisitionReasonsDataDto,
)

internal data class AcquisitionReasonsDataDto(
    val type: String,
    val reasons: List<AcquisitionReasonDto>,
)

// A missing is_note_required reads as false - Gson leaves an absent primitive Boolean at its JVM
// default rather than failing.
internal data class AcquisitionReasonDto(
    val id: Int,
    val reason: String,
    @SerializedName("is_note_required") val isNoteRequired: Boolean = false,
)

// "type" here is "approve"/"reject", a different vocabulary from AcquisitionReasonsDataDto's
// "type" ("accept"/"reject") - the backend just uses different words for the two endpoints.
internal data class AcquisitionDecisionRequestDto(
    val type: String,
    @SerializedName("reason_ids") val reasonIds: List<Int>,
    val note: String,
    @SerializedName("score_agreement") val scoreAgreement: String,
)

internal data class AcquisitionDecisionResponseDto(
    @SerializedName("is_error") val isError: Boolean,
    val message: String,
)
