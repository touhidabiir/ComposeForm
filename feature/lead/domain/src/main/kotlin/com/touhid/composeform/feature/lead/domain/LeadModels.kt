package com.touhid.composeform.feature.lead.domain

import com.touhid.composeform.lead.domain.LeadCloser
import com.touhid.composeform.lead.domain.Rejection
import com.touhid.composeform.lead.domain.Reviewer

// The lead dashboard's domain models. Names and fields deliberately match the wire DTOs they're
// mapped from (feature:lead:data), so this phase of the restructure changed the screen's imports
// rather than its code.

data class LeadDashboardPage(
    val count: Int,
    val pageNo: Int,
    val pageSize: Int,
    val totalPages: Int,
    val results: List<LeadListItem>,
)

enum class LeadStatus { Pending, Approved, Rejected }

data class EkycSubmitter(
    val name: String,
)

data class LeadListItem(
    val id: Long,
    val displayId: String,
    val shopName: String,
    val walletNumber: String,
    val address: String,
    val status: LeadStatus,
    val premiumnessScore: Double,
    val canSubmitEkyc: Boolean,
    val leadCloser: LeadCloser,
    val reviewer: Reviewer? = null,
    val ekycSubmitter: EkycSubmitter? = null,
    val rejection: Rejection? = null,
    val createdAt: String,
)
