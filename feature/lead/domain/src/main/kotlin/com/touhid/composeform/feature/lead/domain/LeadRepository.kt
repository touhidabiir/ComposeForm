package com.touhid.composeform.feature.lead.domain

import com.touhid.composeform.model.AppResult

// Implemented by feature:lead:data (LeadRepositoryImpl), bound with Hilt. ViewModels inject this
// interface, never the implementation, so a test can pass a fake.
interface LeadRepository {

    // status is the backend's own filter value (LeadStatusFilter.apiValue in the ui module), or
    // null when searching - the endpoint filters by one or the other.
    suspend fun getLeads(status: String?, search: String?, pageNo: Int): AppResult<LeadDashboardPage>

    suspend fun submitEkyc(leadId: Long): AppResult<Unit>
}
