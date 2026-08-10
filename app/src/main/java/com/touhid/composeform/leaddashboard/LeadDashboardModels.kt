package com.touhid.composeform.leaddashboard

import com.touhid.composeform.network.model.LeadListItem

// The dashboard's filter tabs, each backed by a distinct "status" query value the leads endpoint
// accepts - "e-KYC" isn't a raw lead status the way pending/approved/rejected are (an item's own
// LeadListItem.status never holds it), it's the backend's own dedicated filter value for
// approved-and-ekyc-submitted leads.
enum class LeadStatusFilter(val label: String, val apiValue: String) {
    Approved("অনুমোদিত", "approved"),
    Rejected("বাতিল", "rejected"),
    Pending("পেন্ডিং", "submitted"),
    EKyc("ই-কেওয়াইসি", "ekyc_submitted"),
}

val LeadListItem.isEkycSubmitted: Boolean get() = ekycSubmitter != null
