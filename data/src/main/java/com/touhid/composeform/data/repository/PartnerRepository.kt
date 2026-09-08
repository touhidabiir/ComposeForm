package com.touhid.composeform.data.repository

import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.api.PartnerApiService
import com.touhid.composeform.network.model.PartnerStatusResponse
import com.touhid.composeform.network.safeApiCall
import javax.inject.Inject

class PartnerRepository @Inject constructor(
    private val apiService: PartnerApiService,
) {

    suspend fun getStatus(): NetworkResult<PartnerStatusResponse> =
        safeApiCall { apiService.getStatus() }
}
