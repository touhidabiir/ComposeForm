package com.touhid.composeform.feature.acquisition.data

import com.touhid.composeform.feature.acquisition.domain.AcquisitionDetail
import com.touhid.composeform.feature.acquisition.domain.AcquisitionListPage
import com.touhid.composeform.feature.acquisition.domain.AcquisitionReason
import com.touhid.composeform.feature.acquisition.domain.AcquisitionRepository
import com.touhid.composeform.model.AppResult
import com.touhid.composeform.model.map
import com.touhid.composeform.network.safeApiCall
import javax.inject.Inject

internal class AcquisitionRepositoryImpl @Inject constructor(
    private val api: AcquisitionApi,
) : AcquisitionRepository {

    override suspend fun getAcquisitionList(search: String?, pageNo: Int): AppResult<AcquisitionListPage> =
        safeApiCall { api.getAcquisitionList(search, pageNo).data }.map { it.toDomain() }

    override suspend fun getAcquisitionDetail(leadId: String): AppResult<AcquisitionDetail> =
        safeApiCall { api.getAcquisitionDetail(leadId).data }.map { it.toDomain() }

    override suspend fun getAcquisitionReasons(leadId: String, type: String): AppResult<List<AcquisitionReason>> =
        safeApiCall { api.getAcquisitionReasons(leadId, type).data.reasons }.map { reasons -> reasons.map { it.toDomain() } }

    // The response body's is_error/message are ignored, as before this moved here: success is
    // decided by the HTTP status alone (see ErrorInterceptor in :core:network).
    override suspend fun submitAcquisitionDecision(
        leadId: String,
        type: String,
        reasonIds: List<Int>,
        note: String,
        scoreAgreement: String,
    ): AppResult<Unit> {
        val request = AcquisitionDecisionRequestDto(type = type, reasonIds = reasonIds, note = note, scoreAgreement = scoreAgreement)
        return safeApiCall { api.submitAcquisitionDecision(leadId, request) }.map { }
    }
}
