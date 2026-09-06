package com.touhid.composeform.network.api

import com.touhid.composeform.network.model.PaymentChargeRequest
import com.touhid.composeform.network.model.PaymentChargeResponse
import retrofit2.http.Body
import retrofit2.http.POST

internal interface PaymentApiService {

    @POST("v1/payment/charge")
    suspend fun charge(@Body request: PaymentChargeRequest): PaymentChargeResponse
}
