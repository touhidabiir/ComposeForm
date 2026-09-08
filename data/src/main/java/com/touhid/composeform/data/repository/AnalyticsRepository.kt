package com.touhid.composeform.data.repository

import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.api.AnalyticsApiService
import com.touhid.composeform.network.model.AnalyticsEventRequest
import com.touhid.composeform.network.safeApiCall
import javax.inject.Inject

class AnalyticsRepository @Inject constructor(
    private val apiService: AnalyticsApiService,
) {

    suspend fun trackEvent(name: String, properties: Map<String, String> = emptyMap()): NetworkResult<Unit> =
        when (val result = safeApiCall { apiService.trackEvent(AnalyticsEventRequest(name, properties)) }) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Failure -> result
        }
}
