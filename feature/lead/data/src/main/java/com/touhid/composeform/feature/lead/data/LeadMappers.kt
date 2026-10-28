package com.touhid.composeform.feature.lead.data

import com.touhid.composeform.feature.lead.domain.EkycSubmitter
import com.touhid.composeform.feature.lead.domain.LeadDashboardPage
import com.touhid.composeform.feature.lead.domain.LeadListItem
import com.touhid.composeform.feature.lead.domain.LeadStatus
import com.touhid.composeform.lead.domain.LeadCloser
import com.touhid.composeform.lead.domain.Rejection
import com.touhid.composeform.lead.domain.RejectionReason
import com.touhid.composeform.lead.domain.Reviewer
import com.touhid.composeform.network.model.LeadCloser as LeadCloserDto
import com.touhid.composeform.network.model.Rejection as RejectionDto
import com.touhid.composeform.network.model.RejectionReason as RejectionReasonDto
import com.touhid.composeform.network.model.Reviewer as ReviewerDto

internal fun LeadDashboardPageDto.toDomain() = LeadDashboardPage(
    count = count,
    pageNo = pageNo,
    pageSize = pageSize,
    totalPages = totalPages,
    results = results.map { it.toDomain() },
)

internal fun LeadListItemDto.toDomain() = LeadListItem(
    id = id,
    displayId = displayId,
    shopName = shopName,
    walletNumber = walletNumber,
    address = address,
    status = status.toDomain(),
    premiumnessScore = premiumnessScore,
    canSubmitEkyc = canSubmitEkyc,
    leadCloser = leadCloser.toDomain(),
    reviewer = reviewer?.toDomain(),
    ekycSubmitter = ekycSubmitter?.let { EkycSubmitter(name = it.name) },
    rejection = rejection?.toDomain(),
    createdAt = createdAt,
)

internal fun LeadStatusDto.toDomain() = when (this) {
    LeadStatusDto.Pending -> LeadStatus.Pending
    LeadStatusDto.Approved -> LeadStatus.Approved
    LeadStatusDto.Rejected -> LeadStatus.Rejected
}

internal fun LeadCloserDto.toDomain() = LeadCloser(
    name = name,
    employeeId = employeeId,
    whitelistingNumber = whitelistingNumber,
    servingMa = servingMa,
)

internal fun ReviewerDto.toDomain() = Reviewer(
    name = name,
    designation = designation,
    servingMa = servingMa,
    hierarchyKey = hierarchyKey,
    hierarchyValue = hierarchyValue,
)

internal fun RejectionDto.toDomain() = Rejection(
    reasons = reasons.map { it.toDomain() },
    note = note,
    reviewedAt = reviewedAt,
    reviewer = reviewer?.toDomain(),
)

internal fun RejectionReasonDto.toDomain() = RejectionReason(id = id, reason = reason)
