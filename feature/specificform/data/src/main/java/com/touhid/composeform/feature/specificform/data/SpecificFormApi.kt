package com.touhid.composeform.feature.specificform.data

import retrofit2.http.GET

// The specific-form endpoint on the main backend (@BaseUrl). Created from :core:network's main
// Retrofit in SpecificFormDataModule, so it shares that client's auth, token refresh, error
// mapping and debug mocks.
internal interface SpecificFormApi {

    @GET("v1/forms/specific")
    suspend fun getSpecificForm(): SpecificFormResponseDto
}
