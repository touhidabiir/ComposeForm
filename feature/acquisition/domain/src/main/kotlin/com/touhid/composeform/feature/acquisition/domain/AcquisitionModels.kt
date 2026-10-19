package com.touhid.composeform.feature.acquisition.domain

import com.touhid.composeform.lead.domain.LeadCloser
import com.touhid.composeform.lead.domain.Rejection

// The acquisition approval flow's domain models. Names and fields deliberately match the wire
// DTOs they're mapped from (feature:acquisition:data), so this phase of the restructure changed
// the screens' imports rather than their code.

data class AcquisitionListPage(
    val count: Int,
    val pageNo: Int,
    val pageSize: Int,
    val totalPages: Int,
    val results: List<AcquisitionListItem>,
)

data class AcquisitionListItem(
    val id: Long,
    val displayId: String,
    val shopName: String,
    val walletNumber: String,
    val address: String,
    val leadCloser: LeadCloser,
    val submittedAt: String,
    val canReview: Boolean,
    // How many times this lead was rejected before its current submission - 0 for a first-time
    // submission, in which case the list card shows no "previously rejected" line at all.
    val rejectionCount: Int = 0,
)

data class AcquisitionDetail(
    val id: Long,
    val displayId: String,
    val shopName: String,
    val walletNumber: String,
    val status: String,
    val premiumnessScore: Double,
    val premiumnessScoreRanges: List<PremiumnessScoreRange>,
    val images: AcquisitionImages,
    val outletInfo: OutletInfo,
    val digitalPayment: DigitalPayment,
    val contactInfo: ContactInfo,
    val walletInfo: WalletInfo,
    val surveyResponses: List<SurveyResponse>,
    val audit: AcquisitionAudit,
    // The lead's earlier rejections, oldest first (index 0 is the 1st rejection). Nullable to keep
    // the screen's existing null checks; the backend may omit the key for a first-time submission.
    val rejectionReasons: List<Rejection>? = null,
)

// One tier of the premiumness score gauge - min/max score, whether this detail's score falls in
// it, and the color its bar segment should render in. The backend drives all of this (bounds,
// which tier is active, and the color), rather than a client-side hardcoded band table.
data class PremiumnessScoreRange(
    val minScore: Int,
    val maxScore: Int,
    val isActive: Boolean,
    val color: String,
)

data class AcquisitionImages(
    val shopImageOutside: String,
    val shopImageInside: String,
    val businessProofImage: String,
)

data class OutletInfo(
    val address: String,
    val district: String,
    val thana: String,
    val marketName: String,
    val bmccCode: String,
    val bmccName: String,
    val productType: String,
    val outletLocationType: String,
    val outletType: String,
)

data class DigitalPayment(
    val cardPaymentAvailable: Boolean,
    val otherMfsAvailable: Boolean,
    val facilities: List<Facility>,
)

data class Facility(val name: String, val completed: Boolean)

data class ContactInfo(
    val contactPerson: ContactPerson,
    val outletOwner: ContactPerson,
)

data class ContactPerson(
    val name: String,
    val phoneNumber: String,
    val designation: String? = null,
)

data class WalletInfo(
    val proposedWalletNumber: String,
    val simStaysAtOutlet: Boolean,
    val simUsedInSmartphone: Boolean,
    val simOwnedByShopOwner: Boolean,
)

data class SurveyResponse(
    val question: String,
    val answer: String,
    val points: Double?,
)

data class AcquisitionAudit(
    val createdAt: String,
    val submittedAt: String,
    val submittedBy: LeadCloser,
)

// isNoteRequired: selecting a reason flagged true (e.g. the backend's own "অন্যান্য"/Other entry)
// makes the sheet's note field mandatory.
data class AcquisitionReason(
    val id: Int,
    val reason: String,
    val isNoteRequired: Boolean = false,
)
