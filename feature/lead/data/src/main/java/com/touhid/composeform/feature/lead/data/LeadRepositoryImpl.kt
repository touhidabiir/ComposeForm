package com.touhid.composeform.feature.lead.data

import com.touhid.composeform.feature.lead.domain.LeadDashboardPage
import com.touhid.composeform.feature.lead.domain.LeadRepository
import com.touhid.composeform.model.AppResult
import com.touhid.composeform.model.map
import com.touhid.composeform.network.safeApiCall
import javax.inject.Inject

internal class LeadRepositoryImpl @Inject constructor(
    private val api: LeadApi,
) : LeadRepository {

    override suspend fun getLeads(status: String?, search: String?, pageNo: Int): AppResult<LeadDashboardPage> =
        safeApiCall { api.getLeads(status, search, pageNo).data }.map { it.toDomain() }

    // The response body's is_error/message are ignored, as before this moved here: success is
    // decided by the HTTP status alone (see ErrorInterceptor in :core:network).
    override suspend fun submitEkyc(leadId: Long): AppResult<Unit> =
        safeApiCall { api.submitEkyc(leadId) }.map { }
}
