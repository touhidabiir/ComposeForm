package com.touhid.composeform.data.repository

import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.api.AppApiService
import com.touhid.composeform.network.auth.TokenProvider
import com.touhid.composeform.network.model.AcquisitionDecisionRequest
import com.touhid.composeform.network.model.AcquisitionDetail
import com.touhid.composeform.network.model.AcquisitionListPage
import com.touhid.composeform.network.model.AcquisitionReason
import com.touhid.composeform.network.model.AdminDetails
import com.touhid.composeform.network.model.AdminSummary
import com.touhid.composeform.network.model.LeadDashboardPage
import com.touhid.composeform.network.model.LoginRequest
import com.touhid.composeform.network.model.LoginResponse
import com.touhid.composeform.network.model.ManagerSummary
import com.touhid.composeform.network.model.SpecificFormPayload
import com.touhid.composeform.network.safeApiCall
import javax.inject.Inject

class DefaultAppRepository @Inject constructor(
    private val apiService: AppApiService,
    private val tokenProvider: TokenProvider,
) : AppRepository {

    override suspend fun login(username: String, password: String): NetworkResult<LoginResponse> =
        safeApiCall { apiService.login(LoginRequest(username, password)) }.also { result ->
            if (result is NetworkResult.Success) {
                tokenProvider.setToken(result.data.token)
            }
        }

    override suspend fun getManagerList(): NetworkResult<List<ManagerSummary>> =
        safeApiCall { apiService.getManagerList() }

    override suspend fun getAdminList(): NetworkResult<List<AdminSummary>> =
        safeApiCall { apiService.getAdminList() }

    override suspend fun getAdminDetails(id: String): NetworkResult<AdminDetails> =
        safeApiCall { apiService.getAdminDetails(id) }

    override suspend fun getSpecificForm(): NetworkResult<SpecificFormPayload> =
        safeApiCall { apiService.getSpecificForm().data }

    override suspend fun getLeadDashboard(status: String?, search: String?, pageNo: Int): NetworkResult<LeadDashboardPage> =
        safeApiCall { apiService.getLeadDashboard(status, search, pageNo).data }

    override suspend fun submitEkyc(leadId: Long): NetworkResult<Unit> =
        when (val result = safeApiCall { apiService.submitEkyc(leadId) }) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Failure -> result
        }

    override suspend fun getAcquisitionList(search: String?, pageNo: Int): NetworkResult<AcquisitionListPage> =
        safeApiCall { apiService.getAcquisitionList(search, pageNo).data }

    override suspend fun getAcquisitionDetail(leadId: String): NetworkResult<AcquisitionDetail> =
        safeApiCall { apiService.getAcquisitionDetail(leadId).data }

    override suspend fun getAcquisitionReasons(leadId: String, type: String): NetworkResult<List<AcquisitionReason>> =
        safeApiCall { apiService.getAcquisitionReasons(leadId, type).data.reasons }

    override suspend fun submitAcquisitionDecision(
        leadId: String,
        type: String,
        reasonIds: List<Int>,
        note: String,
        scoreAgreement: String,
    ): NetworkResult<Unit> {
        val request = AcquisitionDecisionRequest(type = type, reasonIds = reasonIds, note = note, scoreAgreement = scoreAgreement)
        return when (val result = safeApiCall { apiService.submitAcquisitionDecision(leadId, request) }) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Failure -> result
        }
    }
}
