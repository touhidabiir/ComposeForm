package com.touhid.composeform.feature.specificform.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// The specific-form endpoints on the main backend (@BaseUrl). Created from :core:network's main
// Retrofit in SpecificFormDataModule, so they share that client's auth, token refresh, error
// mapping and debug mocks.
internal interface SpecificFormApi {

    @GET("v1/forms/specific")
    suspend fun getSpecificForm(): SpecificFormResponseDto

    @POST("v1/forms/specific/submit")
    suspend fun submitSpecificForm(@Body request: SpecificFormSubmitRequestDto): SpecificFormSubmitResponseDto
}
