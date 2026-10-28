package com.touhid.composeform.lead.data

import com.touhid.composeform.lead.domain.LeadCloser
import com.touhid.composeform.lead.domain.Rejection
import com.touhid.composeform.lead.domain.RejectionReason
import com.touhid.composeform.lead.domain.Reviewer

fun LeadCloserDto.toDomain() = LeadCloser(
    name = name,
    employeeId = employeeId,
    whitelistingNumber = whitelistingNumber,
    servingMa = servingMa,
)

fun ReviewerDto.toDomain() = Reviewer(
    name = name,
    designation = designation,
    servingMa = servingMa,
    hierarchyKey = hierarchyKey,
    hierarchyValue = hierarchyValue,
)

fun RejectionDto.toDomain() = Rejection(
    reasons = reasons.map { it.toDomain() },
    note = note,
    reviewedAt = reviewedAt,
    reviewer = reviewer?.toDomain(),
)

fun RejectionReasonDto.toDomain() = RejectionReason(id = id, reason = reason)
