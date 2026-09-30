package com.touhid.composeform.feature.leaddashboard

import androidx.annotation.StringRes
import com.touhid.composeform.network.model.LeadListItem
import com.touhid.composeform.common.R as CommonR

// The dashboard's filter tabs, each backed by a distinct "status" query value the leads endpoint
// accepts - "e-KYC" isn't a raw lead status the way pending/approved/rejected are (an item's own
// LeadListItem.status never holds it), it's the backend's own dedicated filter value for
// approved-and-ekyc-submitted leads.
// labelRes, not a raw label: String - the display text is a string resource, resolved via
// stringResource() at the composable call site rather than baked in here, since an enum constant
// isn't itself a @Composable context. Approved/Rejected/Pending reuse :common's status_* strings
// (the exact same wording also badges a lead's card in this same file, and :feature:acquisition's
// own status text) - only EKyc's label is leaddashboard-specific, so it stays local.
enum class LeadStatusFilter(@StringRes val labelRes: Int, val apiValue: String) {
    Approved(CommonR.string.common_status_approved, "approved"),
    Rejected(CommonR.string.common_status_rejected, "rejected"),
    Pending(CommonR.string.common_status_pending, "submitted"),
    EKyc(R.string.leaddashboard_status_ekyc, "ekyc_submitted"),
}

val LeadListItem.isEkycSubmitted: Boolean get() = ekycSubmitter != null
