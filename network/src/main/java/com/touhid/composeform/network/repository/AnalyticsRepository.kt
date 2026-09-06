package com.touhid.composeform.network.repository

import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.api.AnalyticsApiService
import com.touhid.composeform.network.model.AnalyticsEventRequest
import com.touhid.composeform.network.safeApiCall
import javax.inject.Inject

// The constructor is internal (not the class) because AnalyticsApiService is internal to
// :network - see AppRepository's constructor for why.
class AnalyticsRepository @Inject internal constructor(
    private val apiService: AnalyticsApiService,
) {

    suspend fun trackEvent(name: String, properties: Map<String, String> = emptyMap()): NetworkResult<Unit> =
        when (val result = safeApiCall { apiService.trackEvent(AnalyticsEventRequest(name, properties)) }) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Failure -> result
        }
}
