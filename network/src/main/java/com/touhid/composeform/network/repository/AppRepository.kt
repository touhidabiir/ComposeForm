package com.touhid.composeform.network.repository

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

// The constructor is internal (not the class) because AppApiService is internal to :network -
// a public constructor can't expose an internal parameter type. Hilt still injects this into
// :app fine: the generated factory that calls this constructor is compiled inside :network,
// where internal is visible; :app only ever sees the AppRepository type itself.
class AppRepository @Inject internal constructor(
    private val apiService: AppApiService,
    private val tokenProvider: TokenProvider,
) {

    suspend fun login(username: String, password: String): NetworkResult<LoginResponse> =
        safeApiCall { apiService.login(LoginRequest(username, password)) }.also { result ->
            if (result is NetworkResult.Success) {
                tokenProvider.setToken(result.data.token)
            }
        }

    suspend fun getManagerList(): NetworkResult<List<ManagerSummary>> =
        safeApiCall { apiService.getManagerList() }

    suspend fun getAdminList(): NetworkResult<List<AdminSummary>> =
        safeApiCall { apiService.getAdminList() }

    suspend fun getAdminDetails(id: String): NetworkResult<AdminDetails> =
        safeApiCall { apiService.getAdminDetails(id) }

    suspend fun getSpecificForm(): NetworkResult<SpecificFormPayload> =
        safeApiCall { apiService.getSpecificForm().data }

    suspend fun getLeadDashboard(status: String?, search: String?, pageNo: Int): NetworkResult<LeadDashboardPage> =
        safeApiCall { apiService.getLeadDashboard(status, search, pageNo).data }

    suspend fun getAcquisitionList(search: String?, pageNo: Int): NetworkResult<AcquisitionListPage> =
        safeApiCall { apiService.getAcquisitionList(search, pageNo).data }

    suspend fun getAcquisitionDetail(leadId: String): NetworkResult<AcquisitionDetail> =
        safeApiCall { apiService.getAcquisitionDetail(leadId).data }

    suspend fun getAcquisitionReasons(leadId: String, type: String): NetworkResult<List<AcquisitionReason>> =
        safeApiCall { apiService.getAcquisitionReasons(leadId, type).data.reasons }

    suspend fun submitAcquisitionDecision(
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
