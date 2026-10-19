package com.touhid.composeform.lead.domain

// Lead value types that more than one feature shows: the lead dashboard (:feature:lead) and the
// acquisition approval flow (:feature:acquisition) both render a lead's closer, reviewer and
// rejection history. Shared, so they live in this core slice rather than inside either feature.

data class LeadCloser(
    val name: String,
    val employeeId: String,
    val whitelistingNumber: String,
    val servingMa: String,
)

data class Reviewer(
    val name: String,
    val designation: String,
    val servingMa: String,
    val hierarchyKey: String,
    val hierarchyValue: String,
)
