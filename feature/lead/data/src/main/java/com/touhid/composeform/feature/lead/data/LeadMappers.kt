package com.touhid.composeform.feature.lead.data

import com.touhid.composeform.feature.lead.domain.EkycSubmitter
import com.touhid.composeform.feature.lead.domain.LeadDashboardPage
import com.touhid.composeform.feature.lead.domain.LeadListItem
import com.touhid.composeform.feature.lead.domain.LeadStatus
import com.touhid.composeform.lead.data.toDomain

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
