package com.touhid.composeform.network.model

import com.google.gson.annotations.SerializedName

// Shared by both the lead-dashboard and acquisition responses, same reason LeadCloser.kt is split
// out - LeadListItem.rejection holds a lead's latest rejection, AcquisitionDetail.rejection_reasons
// its whole history of earlier rejections.
data class Rejection(
    val reasons: List<RejectionReason>,
    val note: String,
    @SerializedName("reviewed_at") val reviewedAt: String,
    // Who rejected this attempt - sent per-entry by the acquisition detail's rejection_reasons
    // history (each attempt can have a different reviewer). The lead-dashboard response omits it
    // and carries its reviewer on LeadListItem.reviewer instead, hence nullable.
    val reviewer: Reviewer? = null,
)

data class RejectionReason(
    val id: Int,
    val reason: String,
)

data class Reviewer(
    val name: String,
    val designation: String,
    @SerializedName("serving_ma") val servingMa: String,
    @SerializedName("hierarchy_key") val hierarchyKey: String,
    @SerializedName("hierarchy_value") val hierarchyValue: String,
)
