package com.touhid.composeform.lead.data

import com.google.gson.annotations.SerializedName

// Wire shapes the lead and acquisition responses both embed (lead_closer, reviewer, rejection
// history). Shared, so they live in this core slice rather than in either feature's data module.
// Public only because two feature data modules parse them; ui modules never depend on this module.

data class LeadCloserDto(
    val name: String,
    @SerializedName("employee_id") val employeeId: String,
    @SerializedName("whitelisting_number") val whitelistingNumber: String,
    @SerializedName("serving_ma") val servingMa: String,
)

data class ReviewerDto(
    val name: String,
    val designation: String,
    @SerializedName("serving_ma") val servingMa: String,
    @SerializedName("hierarchy_key") val hierarchyKey: String,
    @SerializedName("hierarchy_value") val hierarchyValue: String,
)

data class RejectionDto(
    val reasons: List<RejectionReasonDto>,
    val note: String,
    @SerializedName("reviewed_at") val reviewedAt: String,
    // Sent per attempt by the acquisition detail's rejection_reasons history. The lead list omits
    // it and carries its reviewer on the lead item instead, hence nullable.
    val reviewer: ReviewerDto? = null,
)

data class RejectionReasonDto(
    val id: Int,
    val reason: String,
)
