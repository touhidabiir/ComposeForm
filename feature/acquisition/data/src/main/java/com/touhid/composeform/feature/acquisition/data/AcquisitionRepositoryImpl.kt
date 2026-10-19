package com.touhid.composeform.feature.acquisition.data

import com.touhid.composeform.feature.acquisition.domain.AcquisitionDetail
import com.touhid.composeform.feature.acquisition.domain.AcquisitionListPage
import com.touhid.composeform.feature.acquisition.domain.AcquisitionReason
import com.touhid.composeform.feature.acquisition.domain.AcquisitionRepository
import com.touhid.composeform.model.AppResult
import com.touhid.composeform.model.map
import com.touhid.composeform.network.safeAppCall
import javax.inject.Inject

internal class AcquisitionRepositoryImpl @Inject constructor(
    private val api: AcquisitionApi,
) : AcquisitionRepository {

    override suspend fun getAcquisitionList(search: String?, pageNo: Int): AppResult<AcquisitionListPage> =
        safeAppCall { api.getAcquisitionList(search, pageNo).data }.map { it.toDomain() }

    override suspend fun getAcquisitionDetail(leadId: String): AppResult<AcquisitionDetail> =
        safeAppCall { api.getAcquisitionDetail(leadId).data }.map { it.toDomain() }

    override suspend fun getAcquisitionReasons(leadId: String, type: String): AppResult<List<AcquisitionReason>> =
        safeAppCall { api.getAcquisitionReasons(leadId, type).data.reasons }.map { reasons -> reasons.map { it.toDomain() } }

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
        return safeAppCall { api.submitAcquisitionDecision(leadId, request) }.map { }
    }
}
