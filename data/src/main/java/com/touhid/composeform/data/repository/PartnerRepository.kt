package com.touhid.composeform.data.repository

import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.model.PartnerStatusResponse

interface PartnerRepository {
    suspend fun getStatus(): NetworkResult<PartnerStatusResponse>
}
