package com.touhid.composeform.data.repository

import com.touhid.composeform.network.NetworkResult

interface AnalyticsRepository {
    suspend fun trackEvent(name: String, properties: Map<String, String> = emptyMap()): NetworkResult<Unit>
}
