package com.touhid.composeform.network.model

import com.google.gson.annotations.SerializedName

// Shared by both the lead-dashboard and acquisition responses - the backend returns the same
// lead-closer shape in both places (LeadListItem.lead_closer, AcquisitionListItem.lead_closer,
// AcquisitionAudit.submitted_by).
data class LeadCloser(
    val name: String,
    @SerializedName("employee_id") val employeeId: String,
    @SerializedName("whitelisting_number") val whitelistingNumber: String,
    @SerializedName("serving_ma") val servingMa: String,
)
