package com.touhid.composeform.lead.domain

data class Rejection(
    val reasons: List<RejectionReason>,
    val note: String,
    // Raw "yyyy/MM/dd hh:mm:ss a" timestamp; :core:common's formatRejectionTimestamp formats it.
    val reviewedAt: String,
    // Set per attempt in the acquisition rejection history; the lead dashboard carries its
    // reviewer on LeadListItem.reviewer instead, hence nullable.
    val reviewer: Reviewer? = null,
)

data class RejectionReason(
    val id: Int,
    val reason: String,
)
