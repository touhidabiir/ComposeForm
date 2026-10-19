package com.touhid.composeform.feature.acquisition.domain

import com.touhid.composeform.model.AppResult

interface AcquisitionRepository {

    suspend fun getAcquisitionList(search: String?, pageNo: Int): AppResult<AcquisitionListPage>

    suspend fun getAcquisitionDetail(leadId: String): AppResult<AcquisitionDetail>

    // type: the reasons API's own vocabulary, "accept" or "reject".
    suspend fun getAcquisitionReasons(leadId: String, type: String): AppResult<List<AcquisitionReason>>

    // type: the decision API's own vocabulary, "approve" or "reject" (not the same words as
    // getAcquisitionReasons - the backend just differs between the two endpoints).
    suspend fun submitAcquisitionDecision(
        leadId: String,
        type: String,
        reasonIds: List<Int>,
        note: String,
        scoreAgreement: String,
    ): AppResult<Unit>
}
