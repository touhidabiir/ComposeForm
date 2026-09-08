package com.touhid.composeform.network.repository

import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.api.PartnerApiService
import com.touhid.composeform.network.model.PartnerStatusResponse
import com.touhid.composeform.network.safeApiCall
import javax.inject.Inject

// The constructor is internal (not the class) because PartnerApiService is internal to
// :network - see AppRepository's constructor for why.
class PartnerRepository @Inject internal constructor(
    private val apiService: PartnerApiService,
) {

    suspend fun getStatus(): NetworkResult<PartnerStatusResponse> =
        safeApiCall { apiService.getStatus() }
}
