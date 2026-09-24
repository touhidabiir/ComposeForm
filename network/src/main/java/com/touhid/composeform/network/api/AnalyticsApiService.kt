package com.touhid.composeform.network.api

import com.touhid.composeform.network.model.AnalyticsEventRequest
import com.touhid.composeform.network.model.AnalyticsEventResponse
import retrofit2.http.Body
import retrofit2.http.POST

internal interface AnalyticsApiService {

    @POST("v1/analytics/event")
    suspend fun trackEvent(@Body request: AnalyticsEventRequest): AnalyticsEventResponse
}
