package com.touhid.composeform.network.model

data class AnalyticsEventRequest(
    val name: String,
    val properties: Map<String, String> = emptyMap(),
)

data class AnalyticsEventResponse(
    val accepted: Boolean,
)
