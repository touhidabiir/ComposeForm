package com.touhid.composeform.data.repository

import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.model.AcquisitionDetail
import com.touhid.composeform.network.model.AcquisitionListPage
import com.touhid.composeform.network.model.AcquisitionReason
import com.touhid.composeform.network.model.AdminDetails
import com.touhid.composeform.network.model.AdminSummary
import com.touhid.composeform.network.model.LeadDashboardPage
import com.touhid.composeform.network.model.LoginResponse
import com.touhid.composeform.network.model.ManagerSummary
import com.touhid.composeform.network.model.SpecificFormPayload

// The contract consumers should inject - DefaultAppRepository is the only implementation today,
// but declaring this as an interface (rather than injecting DefaultAppRepository directly) lets a
// ViewModel test substitute a fake implementation instead of a real AppApiService/TokenProvider.
interface AppRepository {

    suspend fun login(username: String, password: String): NetworkResult<LoginResponse>

    suspend fun getManagerList(): NetworkResult<List<ManagerSummary>>

    suspend fun getAdminList(): NetworkResult<List<AdminSummary>>

    suspend fun getAdminDetails(id: String): NetworkResult<AdminDetails>

    suspend fun getSpecificForm(): NetworkResult<SpecificFormPayload>

    suspend fun getLeadDashboard(status: String?, search: String?, pageNo: Int): NetworkResult<LeadDashboardPage>

    suspend fun submitEkyc(leadId: Long): NetworkResult<Unit>

    suspend fun getAcquisitionList(search: String?, pageNo: Int): NetworkResult<AcquisitionListPage>

    suspend fun getAcquisitionDetail(leadId: String): NetworkResult<AcquisitionDetail>

    suspend fun getAcquisitionReasons(leadId: String, type: String): NetworkResult<List<AcquisitionReason>>

    suspend fun submitAcquisitionDecision(
        leadId: String,
        type: String,
        reasonIds: List<Int>,
        note: String,
        scoreAgreement: String,
    ): NetworkResult<Unit>
}
