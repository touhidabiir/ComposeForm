package com.touhid.composeform.data.repository.impl

import com.touhid.composeform.data.repository.PartnerRepository
import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.api.PartnerApiService
import com.touhid.composeform.network.model.PartnerStatusResponse
import com.touhid.composeform.network.safeApiCall
import javax.inject.Inject

class PartnerRepositoryImpl @Inject constructor(
    private val apiService: PartnerApiService,
) : PartnerRepository {

    override suspend fun getStatus(): NetworkResult<PartnerStatusResponse> =
        safeApiCall { apiService.getStatus() }
}
